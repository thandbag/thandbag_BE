package com.example.thandbag.repository;

import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;
    User user3;
    User user4;

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
        ChatRoom chatRoom1 = new ChatRoom(
                "chatRoom1",
                user1.getId(),
                user2.getId()
        );

        ChatRoom chatRoom2 = new ChatRoom(
                "chatRoom2",
                user1.getId(),
                user3.getId()
        );

        // DB 저장
        chatRoomRepository.save(chatRoom1);
        chatRoomRepository.save(chatRoom2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        //when
        List<ChatRoom> result = chatRoomRepository.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals(user1.getId(), result.get(0).getPubUserId());
        assertEquals(user3.getId(), result.get(1).getSubUserId());
        assertEquals("chatRoom1", result.get(0).getId());
        assertEquals("chatRoom2", result.get(1).getId());
    }

    @Order(2)
    @DisplayName("성공 - PubID,SubID검색")
    @Test
    void saveAnfindAllByPubUserIdOrSubUserIddFindAll() {

        //when
        List<ChatRoom> result = chatRoomRepository.findAllByPubUserIdOrSubUserId(user1.getId(), user1.getId());

        //then
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getId());
        assertEquals("chatRoom2", result.get(1).getId());
    }

    @Order(3)
    @DisplayName("성공 - PubID+SubID검색")
    @Test
    void findByPubUserIdAndSubUserId() {

        //when
        ChatRoom result = chatRoomRepository.findByPubUserIdAndSubUserId(user1.getId(), user2.getId());

        //then
        assertNotNull(result);
        assertNotNull(result.getPubUserId());
        assertNotNull(result.getSubUserId());
    }

    @Order(4)
    @DisplayName("성공 - PubID+SubID검색(boolean)")
    @Test
    void existsAllByPubUserIdAndSubUserId() {

        //when
        Boolean result = chatRoomRepository.existsAllByPubUserIdAndSubUserId(user1.getId(), user3.getId());

        //then
        assertTrue(result);
    }

    @Order(5)
    @DisplayName("결과없음 - PubID,SubID검색")
    @Test
    void saveAnfindAllByPubUserIdOrSubUserIddFindAll2() {

        //when
        List<ChatRoom> result = chatRoomRepository.findAllByPubUserIdOrSubUserId(user4.getId(), user4.getId());

        //then
        assertEquals(0, result.size());
    }

    @Order(6)
    @DisplayName("결과없음 - PubID+SubID검색")
    @Test
    void findByPubUserIdAndSubUserId2() {

        //when
        ChatRoom result = chatRoomRepository.findByPubUserIdAndSubUserId(user1.getId(), user4.getId());

        //then
        assertNull(result);
    }

    @Order(7)
    @DisplayName("결과없음 - PubID+SubID검색(boolean)")
    @Test
    void existsAllByPubUserIdAndSubUserId2() {

        //when
        Boolean result = chatRoomRepository.existsAllByPubUserIdAndSubUserId(user1.getId(), user4.getId());

        //then
        assertFalse(result);
    }
}