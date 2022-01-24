package com.example.thandbag.service;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.MessageType;
import com.example.thandbag.dto.chat.ChatHistoryResponseDto;
import com.example.thandbag.dto.chat.ChatMessageDto;
import com.example.thandbag.dto.chat.ChatMyRoomListResponseDto;
import com.example.thandbag.dto.chat.chatroom.ChatRoomDto;
import com.example.thandbag.dto.chat.chatroom.CreateRoomRequestDto;
import com.example.thandbag.model.ChatContent;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    ChannelTopic channelTopic;
    @Mock
    RedisTemplate redisTemplate;
    @Mock
    ChatRedisRepository chatRedisRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    ChatContentRepository chatContentRepository;
    @Mock
    AlarmRepository alarmRepository;

    @Order(1)
    @DisplayName("닉네임 찾기")
    @Test
    void getNickname() {

        /* given */
        ChatService chatService = new ChatService(
                channelTopic,
                redisTemplate,
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                chatRedisRepository,
                userRepository,
                chatRoomRepository,
                chatContentRepository,
                alarmRepository);

        String username = user.getUsername();
        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(user));

        /* when */
        String result = chatService.getNickname(username);

        /* then */
        assertEquals(user.getNickname(), result);
    }

    @Order(2)
    @DisplayName("메시지 발송")
    @Test
    void sendMessage() {
        /* given */
        ChatService chatService = new ChatService(
                channelTopic,
                redisTemplate,
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                chatRedisRepository,
                userRepository,
                chatRoomRepository,
                chatContentRepository,
                alarmRepository);

        ChatMessageDto chatMessageDto = new ChatMessageDto(
                MessageType.ENTER,
                "asdf1234",
                user.getNickname(),
                "테스트메시지",
                1,
                "2022.01.13"
        );

        /* when */
        chatService.sendChatMessage(chatMessageDto);

        /* then */
        assertEquals("[알림]", chatMessageDto.getSender());
    }

    @Order(3)
    @DisplayName("채팅방 생성")
    @Test
    void createChatRoom() {
        /* given */
        ChatService chatService = new ChatService(
                channelTopic,
                redisTemplate,
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                chatRedisRepository,
                userRepository,
                chatRoomRepository,
                chatContentRepository,
                alarmRepository);

        CreateRoomRequestDto roomRequestDto = new CreateRoomRequestDto(
                user.getId(), user2.getId()
        );

        given(chatRoomRepository
                        .existsAllByPubUserIdAndSubUserId(anyLong(), anyLong())
                || chatRoomRepository
                        .existsAllByPubUserIdAndSubUserId(anyLong(), anyLong()))
                .willReturn(false);

        chatRoomDto = new ChatRoomDto(roomRequestDto);

        given(chatRedisRepository.createChatRoom(roomRequestDto))
                .willReturn(chatRoomDto);
        given(userRepository.getById(roomRequestDto.getPubId()))
                .willReturn(user);

        /* when */
        ChatRoomDto result = chatService.createChatRoom(roomRequestDto);

        /* then */
        assertEquals(user.getId() ,result.getPubId());
        assertEquals(user2.getId() ,result.getSubId());
    }

    @Order(4)
    @DisplayName("내가 참여한 채팅방")
    @Test
    void getChatRoomList() {
        /* given */
        ChatService chatService = new ChatService(
                channelTopic,
                redisTemplate,
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                chatRedisRepository,
                userRepository,
                chatRoomRepository,
                chatContentRepository,
                alarmRepository);

        ChatRoomDto chatRoomDto = new ChatRoomDto(
                "ThisIsChatRoomId",
                user.getId(),
                user2.getId()
        );

        List<ChatRoom> chatRoomList = new ArrayList<>();
        ChatRoom chatRoom = new ChatRoom(
                chatRoomDto.getRoomId(),
                chatRoomDto.getPubId(),
                chatRoomDto.getSubId()
        );
        chatRoom.setCreatedAt(LocalDateTime.now());
        chatRoomList.add(chatRoom);

        given(chatRoomRepository
                .findAllByPubUserIdOrSubUserId(user.getId(), user.getId()))
                .willReturn(chatRoomList);
        given(userRepository.getById(anyLong())).willReturn(user2);


        /* when */
        List<ChatMyRoomListResponseDto> result = chatService.findMyChatList(user);

        /* then */
        assertEquals(1, result.size());
        assertEquals(chatRoom.getId(), result.get(0).getRoomId());
        assertEquals(user2.getNickname(), result.get(0).getSubNickname());
    }

    @Order(5)
    @DisplayName("이전 대화목록 불러오기")
    @Test
    void getChatList() {
        /* given */
        ChatService chatService = new ChatService(
                channelTopic,
                redisTemplate,
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                chatRedisRepository,
                userRepository,
                chatRoomRepository,
                chatContentRepository,
                alarmRepository);

        String roomId = "ThisIsChatRoomId";
        Long pubId = user.getId();
        Long subId = user2.getId();

        /* 메세지 생성 */
        ChatRoom room = new ChatRoom(
                roomId,
                pubId,
                subId
        );

        ChatContent chatContent1 = new ChatContent(
                1L,
                "메시지1",
                user,
                room,
                false
        );
        chatContent1.setCreatedAt(LocalDateTime.now());

        ChatContent chatContent2 = new ChatContent(
                2L,
                "메시지2",
                user,
                room,
                false
        );
        chatContent2.setCreatedAt(LocalDateTime.now());

        ChatContent chatContent3 = new ChatContent(
                3L,
                "메시지3",
                user,
                room,
                false
        );
        chatContent3.setCreatedAt(LocalDateTime.now());

        List<ChatContent> chatContentList = new ArrayList<>();
        chatContentList.add(chatContent1);
        chatContentList.add(chatContent2);
        chatContentList.add(chatContent3);

        given(chatRoomRepository.getById(anyString())).willReturn(room);
        given(chatContentRepository
                .findAllByChatRoomOrderByCreatedAtAsc(any(ChatRoom.class)))
                .willReturn(chatContentList);

        /* when */
        List<ChatHistoryResponseDto> result =
                chatService.getTotalChatContents(room.getId());

        /* then */
        assertEquals(3, result.size());
        assertEquals(chatContent1.getUser().getProfileImg().getProfileImgUrl(),
                result.get(0).getSenderProfileImg());
        assertEquals(chatContent2.getUser().getNickname(),
                result.get(1).getSender());
        assertEquals(chatContent3.getContent(),
                result.get(2).getMessage());
    }

    /* 정보 수정 확인을 위한 유저 생성 */
    ProfileImg profileImg = new ProfileImg(
            1L,
            "naver.com"
    );
    User user = new User(
            1L,
            null,
            "hanghae99@hanghae99.kr",
            "test1234!@",
            "생드백",
            "ENTP",
            0,
            1,
            Auth.USER,
            profileImg
    );

    User user2 = new User(
            2L,
            null,
            "hanghae999@hanghae99.kr",
            "test1234!@",
            "생드백2",
            "ENTP",
            0,
            1,
            Auth.USER,
            profileImg
    );

    ChatRoomDto chatRoomDto;

}