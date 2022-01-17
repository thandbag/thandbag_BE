package com.example.thandbag.repository;

import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.ChatContent;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class ChatContentRepositoryTest {

    @Autowired
    ChatContentRepository chatContentRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;
    User user3;
    User user4;
    ChatRoom chatRoom1;
    ChatRoom chatRoom2;

    @BeforeEach
    void setup() {
        // 유저 생성
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@test.kr",
                "테스트",
                "test1234!@",
                "INFJ"
        );
        this.user1 = new User(signupRequestDto);

        signupRequestDto = new SignupRequestDto(
                "test2@test.kr",
                "테스트2",
                "test1234!@",
                "INFJ"
        );
        this.user2 = new User(signupRequestDto);

        signupRequestDto = new SignupRequestDto(
                "test3@test.kr",
                "테스트3",
                "test1234!@",
                "INFJ"
        );
        this.user3 = new User(signupRequestDto);

        signupRequestDto = new SignupRequestDto(
                "test4@test.kr",
                "테스트4",
                "test1234!@",
                "INFJ"
        );
        this.user4 = new User(signupRequestDto);

        // DB 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        // ChatRoom 생성
        chatRoom1 = new ChatRoom(
                "chatRoom1",
                user1.getId(),
                user2.getId()
        );

        chatRoom2 = new ChatRoom(
                "chatRoom2",
                user1.getId(),
                user3.getId()
        );

        // DB 저장
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);

        // ChatContent 생성
        ChatContent chatContent1 = ChatContent.builder()
                .content("채팅1")
                .user(user1)
                .chatRoom(chatRoom1)
                .isRead(true)
                .build();

        ChatContent chatContent2 = ChatContent.builder()
                .content("채팅2")
                .user(user2)
                .chatRoom(chatRoom1)
                .isRead(false)
                .build();

        ChatContent chatContent3 = ChatContent.builder()
                .content("채팅3")
                .user(user1)
                .chatRoom(chatRoom1)
                .isRead(false)
                .build();

        // DB 저장
        chatContentRepository.save(chatContent1);
        chatContentRepository.save(chatContent2);
        chatContentRepository.save(chatContent3);

    }

    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        //when
        List<ChatContent> result = chatContentRepository.findAll();

        //then
        assertEquals(3, result.size());
        assertEquals("채팅1", result.get(0).getContent());
        assertFalse(result.get(1).getIsRead());
    }

    @Order(2)
    @DisplayName("성공 - 마지막대화검색")
    @Test
    void findFirstByChatRoomOrderByCreatedAtDesc() {

        //when
        Optional<ChatContent> result = chatContentRepository.findFirstByChatRoomOrderByCreatedAtDesc(chatRoom1);

        //then
        assertNotEquals(Optional.empty(), result);
        assertTrue(result.isPresent());
        assertEquals("테스트", result.get().getUser().getNickname());
//        assertEquals("채팅3", result.get().getContent());
    }

    @Order(3)
    @DisplayName("성공 - 전체대화검색")
    @Test
    void findAllByChatRoomOrderByCreatedAtAsc() {

        //when
        List<ChatContent> result = chatContentRepository.findAllByChatRoomOrderByCreatedAtAsc(chatRoom1);

        //then
        assertEquals(3, result.size());
        assertEquals("채팅1", result.get(0).getContent());
        assertEquals("테스트2", result.get(1).getUser().getNickname());
        assertFalse(result.get(2).getIsRead());
    }

    @Order(4)
    @DisplayName("성공 - 읽지않은대화")
    @Test
    void findAllByUserNotAndIsRead() {

        //when
        List<ChatContent> result = chatContentRepository.findAllByUserNotAndChatRoomAndIsRead(user2, chatRoom1, false);

        //then
        assertEquals(1, result.size());
        assertEquals("채팅3", result.get(0).getContent());
    }

    @Order(5)
    @DisplayName("결과없음 - 마지막대화검색")
    @Test
    void findFirstByChatRoomOrderByCreatedAtDesc2() {

        //when
        Optional<ChatContent> result = chatContentRepository.findFirstByChatRoomOrderByCreatedAtDesc(chatRoom2);

        //then
        assertFalse(result.isPresent());
        assertEquals(Optional.empty(), result);
    }

    @Order(6)
    @DisplayName("결과없음 - 전체대화검색")
    @Test
    void findAllByChatRoomOrderByCreatedAtAsc2() {

        //when
        List<ChatContent> result = chatContentRepository.findAllByChatRoomOrderByCreatedAtAsc(chatRoom2);

        //then
        assertEquals(0, result.size());
    }

    @Order(7)
    @DisplayName("결과없음 - 읽지않은대화")
    @Test
    void findAllByUserNotAndIsRead2() {

        //when
        List<ChatContent> result = chatContentRepository.findAllByUserNotAndChatRoomAndIsRead(user2, chatRoom2, false);

        //then
        assertEquals(0, result.size());
    }
}