package com.example.thandbag.service;


import com.example.thandbag.Enum.MessageType;
import com.example.thandbag.dto.chat.ChatHistoryResponseDto;
import com.example.thandbag.dto.chat.ChatMessageDto;
import com.example.thandbag.dto.chat.ChatMyRoomListResponseDto;
import com.example.thandbag.dto.chat.chatroom.ChatRoomDto;
import com.example.thandbag.dto.chat.chatroom.CreateRoomRequestDto;
import com.example.thandbag.model.ChatContent;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final AlarmService alarmService;
    private final ChatRedisRepository chatRedisRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatContentRepository chatContentRepository;
    private final AlarmRepository alarmRepository;


    public String getNickname(String username) {
        return userRepository.findByUsername(username).get().getNickname();
    }

     /* destination정보에서 roomId 추출 */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /* 채팅방에 메시지 발송 */
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        String nickname = chatMessageDto.getSender();
        chatMessageDto.setUserCount(
                chatRedisRepository.getUserCount(chatMessageDto.getRoomId())
        );

        if (MessageType.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender()
                                    + "님이 방에 입장했습니다.");
            chatMessageDto.setSender("[알림]");
        } else if (MessageType.QUIT.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender()
                                    + "님이 방에서 나갔습니다.");
            chatMessageDto.setSender("[알림]");
        } else {
            Optional<User> user = userRepository.findByNickname(nickname);
            Optional<ChatRoom> chatRoom = chatRoomRepository
                    .findById(chatMessageDto.getRoomId());
            Boolean readCheck = chatMessageDto.getUserCount() != 1;

            /* 입장, 퇴장 알림을 제외한 메시지를 MYSQL에 저장 */
            ChatContent chatContent = ChatContent.builder()
                    .content(chatMessageDto.getMessage())
                    .user(user.get())
                    .chatRoom(chatRoom.get())
                    .isRead(readCheck)
                    .build();

            chatContentRepository.save(chatContent);
        }
        /* 채팅 메시지를 redis로 publish */
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);
    }

    /* 채팅방 생성 */
    @Transactional
    public ChatRoomDto createChatRoom(CreateRoomRequestDto roomRequestDto) {
        /* 서로 같은 사람이 다시 생성하려고 하면 안되게 함 */
        if (
                chatRoomRepository.existsAllByPubUserIdAndSubUserId(
                        roomRequestDto.getPubId(),
                        roomRequestDto.getSubId())
             || chatRoomRepository.existsAllByPubUserIdAndSubUserId(
                        roomRequestDto.getSubId(),
                        roomRequestDto.getPubId())
        ) {
            throw new IllegalArgumentException("이미 생성된 채팅방입니다.");
        }

        /* Redis에 채팅방 저장 */
        ChatRoomDto chatRoomDto = chatRedisRepository
                .createChatRoom(roomRequestDto);

        ChatRoom chatRoom = new ChatRoom(
                chatRoomDto.getRoomId(),
                roomRequestDto.getPubId(),
                roomRequestDto.getSubId()
        );

        chatRoomRepository.save(chatRoom);

        /* 알림 생성 */
        alarmService.generateNewChatroomAlarm(roomRequestDto, chatRoom);

        return chatRoomDto;
    }

    /* 내가 참가한 채팅방 목록 */
    public List<ChatMyRoomListResponseDto> findMyChatList(User user) {
        List<ChatRoom> chatRoomList = chatRoomRepository
                .findAllByPubUserIdOrSubUserId(user.getId(), user.getId());

        List<ChatMyRoomListResponseDto> responseDtoList = new ArrayList<>();
        String roomId;
        String subNickname;
        String subProfileImgUrl;
        String lastContent;
        String lastContentCreatedTime;
        int unreadCount;

        for (ChatRoom room : chatRoomList) {
            /* 내가 pub 이면 sub아이디를, sub이면 pub아이디 찾아야 함 */
            roomId = room.getId();
            User subUser = user.getId().equals(room.getSubUserId())
                    ? userRepository.getById(room.getPubUserId())
                    : userRepository.getById(room.getSubUserId());

            subNickname = subUser.getNickname();
            subProfileImgUrl = subUser.getProfileImg().getProfileImgUrl();

            /* 시간 표시 형식 변경 */
            Optional<ChatContent> lastCont = chatContentRepository
                    .findFirstByChatRoomOrderByCreatedAtDesc(room);
            if (lastCont.isPresent()) {
                lastContent = lastCont.get().getContent();
                lastContentCreatedTime = TimeConversion
                        .chattingListTimeConversion(
                                lastCont.get().getCreatedAt()
                        );
            } else {
                lastContent = "";
                lastContentCreatedTime = TimeConversion
                        .chattingListTimeConversion(room.getCreatedAt());
            }

            /* 읽지 않은 메시지 수 */
            unreadCount = chatContentRepository
                    .findAllByUserNotAndChatRoomAndIsRead(
                            user, room, false)
                    .size();

            ChatMyRoomListResponseDto dto = new ChatMyRoomListResponseDto(
                    roomId,
                    subNickname,
                    subProfileImgUrl,
                    lastContent,
                    lastContentCreatedTime,
                    unreadCount
            );
            responseDtoList.add(dto);
        }

        /* 최신 메시지 시간을 기준으로 내림차순 정렬 */
        Collections.sort(responseDtoList);
        Collections.reverse(responseDtoList);

        return responseDtoList;
    }

    /* 채팅방 입장 - 입장시 이전 대화 목록 불러오기 */
    @Transactional
    public List<ChatHistoryResponseDto> getTotalChatContents(String roomId) {
        ChatRoom room = chatRoomRepository.getById(roomId);
        List<ChatContent> chatContentList = chatContentRepository
                .findAllByChatRoomOrderByCreatedAtAsc(room);
        List<ChatHistoryResponseDto> chatHistoryList = new ArrayList<>();
        for (ChatContent chat : chatContentList) {
            String createdTime = TimeConversion
                    .ampmConversion(chat.getCreatedAt());
            if (!chat.getIsRead()) {
                chat.setIsRead(true);
            }
            ChatHistoryResponseDto historyResponseDto =
                    new ChatHistoryResponseDto(
                        chat.getUser().getNickname(),
                        chat.getUser().getProfileImg().getProfileImgUrl(),
                        chat.getContent(),
                        createdTime
            );
            chatHistoryList.add(historyResponseDto);
        }
        return chatHistoryList;
    }
}