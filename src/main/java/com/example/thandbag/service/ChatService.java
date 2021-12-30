package com.example.thandbag.service;


import com.example.thandbag.dto.ChatMessageDto;
import com.example.thandbag.dto.ChatMyRoomListResponseDto;
import com.example.thandbag.dto.ChatRoomDto;
import com.example.thandbag.dto.CreateRoomRequestDto;
import com.example.thandbag.model.ChatContent;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.ChatContentRepository;
import com.example.thandbag.repository.ChatRedisRepository;
import com.example.thandbag.repository.ChatRoomRepository;
import com.example.thandbag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRedisRepository chatRedisRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatContentRepository chatContentRepository;


    public String getNickname(String username) {
        String nickname = userRepository.findByUsername(username).get().getNickname();
        return nickname;
    }

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        String nickname = chatMessageDto.getSender();
        System.out.println("메시지 sender : " + chatMessageDto.getSender());
        System.out.println("메시지 메시지 : " + chatMessageDto.getMessage());
        chatMessageDto.setUserCount(chatRedisRepository.getUserCount(chatMessageDto.getRoomId()));
        if (ChatMessageDto.MessageType.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 방에 입장했습니다.");
            chatMessageDto.setSender("[알림]");
        } else if (ChatMessageDto.MessageType.QUIT.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 방에서 나갔습니다.");
            chatMessageDto.setSender("[알림]");
        } else {
            Optional<User> user = userRepository.findByNickname(nickname);
            Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatMessageDto.getRoomId());

            // 입장, 퇴장 알림을 제외한 메시지를 MYSQL에 저장
            ChatContent chatContent = ChatContent.builder()
                    .content(chatMessageDto.getMessage())
                    .user(user.get())
                    .chatRoom(chatRoom.get())
                    .build();

            chatContentRepository.save(chatContent);
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);
    }


    // 채팅방 생성
    @Transactional
    public ChatRoomDto createChatRoom(CreateRoomRequestDto roomRequestDto) {
        // Redis에 채팅방 저장
        ChatRoomDto chatRoomDto = chatRedisRepository.createChatRoom(roomRequestDto);

        ChatRoom chatRoom = new ChatRoom(
                chatRoomDto.getRoomId(),
                roomRequestDto.getPubId(),
                roomRequestDto.getSubId()
        );
        // RDB에 저장
        chatRoomRepository.save(chatRoom);
        return chatRoomDto;
    }

    // 내가 참가한 채팅방 목록
    public List<ChatMyRoomListResponseDto> findMyChatList(User user) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByPubUserIdOrSubUserId(user.getId(), user.getId());
        List<ChatMyRoomListResponseDto> responseDtoList = new ArrayList<>();
        String roomId;
        String subNickname;
        String subProfileImgUrl;
        String lastContent;
        LocalDateTime lastContentCreatedTime;

        for (ChatRoom room : chatRoomList) {
            // 내가 pub 이면 sub아이디 찾아야 하고, sub이면 pub아이디 찾아야 함
            roomId = room.getId();
            User subUser = user.getId().equals(room.getSubUserId()) ? userRepository.getById(room.getPubUserId()) : userRepository.getById(room.getSubUserId());
            subNickname = subUser.getNickname();
//            subProfileImgUrl = subUser.getProfileImg().getProfileImgUrl();
            subProfileImgUrl = "naver.com/asdfasdf.jpg";

            Optional<ChatContent> lastCont = chatContentRepository.findFirstByChatRoomOrderByCreatedAtDesc(room);
            if (lastCont.isPresent()) {
                lastContent = lastCont.get().getContent();
                lastContentCreatedTime = lastCont.get().getCreatedAt();
            } else {
                lastContent = "";
                lastContentCreatedTime = LocalDateTime.now();
            }

            ChatMyRoomListResponseDto dto = new ChatMyRoomListResponseDto(
                    roomId,
                    subNickname,
                    subProfileImgUrl,
                    lastContent,
                    lastContentCreatedTime
            );
            responseDtoList.add(dto);
        }
        return responseDtoList;
    }
}

//    }
//}