package com.example.thandbag.repository;

import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {

        // 일반 회원가입 유저 생성을 위한, SignupRequestDto 생성
        String username = "test1@test.kr";
        String nickname = "테스트1";
        String password = "test1234!@";
        String mbti = "INFJ";
        Long kakaoId = 11111L;

        // 일반유저1 생성
        SignupRequestDto signupRequestDto = new SignupRequestDto(username, nickname, password, mbti);
        User user1 = new User(signupRequestDto);

        // 일반유저2 생성
        username = "test2@test.kr";
        nickname = "테스트2";
        signupRequestDto = new SignupRequestDto(username, nickname, password, mbti);
        User user2 = new User(signupRequestDto);

        // 일반유저3 생성
        username = "test3@test.kr";
        nickname = "테스트3";
        signupRequestDto = new SignupRequestDto(username, nickname, password, mbti);
        User user3 = new User(signupRequestDto);

        // 카카오유저1 생성
        username = "kakao1@kakao.kr";
        nickname = "카카오1";
        mbti = "KKKK";
        User kakaouser1 = new User(
                username,
                nickname,
                password,
                mbti,
                kakaoId
        );

        // 카카오유저2 생성
        username = "kakao2@kakao.kr";
        nickname = "카카오2";
        kakaoId = 22222L;
        User kakaouser2 = new User(
                username,
                nickname,
                password,
                mbti,
                kakaoId
        );

        // 카카오유저3 생성
        username = "kakao3@kakao.kr";
        nickname = "카카오3";
        kakaoId = 33333L;
        User kakaouser3 = new User(
                username,
                nickname,
                password,
                mbti,
                kakaoId
        );

        // DB에 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(kakaouser1);
        userRepository.save(kakaouser2);
        userRepository.save(kakaouser3);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색 ")
    @Test
    void saveAndFindAll() {

        //when
        List<User> result = userRepository.findAll();

        //then
        assertEquals(6, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(11111L, result.get(3).getKakaoId());
    }

    @Order(2)
    @DisplayName("성공 - username검색")
    @Test
    void findByUsername() {

        //when
        Optional<User> result = userRepository.findByUsername("kakao1@kakao.kr");

        //then
        assertNotEquals(Optional.empty(), result);
        assertEquals("카카오1", result.get().getNickname());
    }

    @Order(3)
    @DisplayName("성공 - nickname검색")
    @Test
    void findByNickname() {

        //when
        Optional<User> result = userRepository.findByNickname("테스트3");

        //then
        assertNotEquals(Optional.empty(), result);
        assertEquals("test3@test.kr", result.get().getUsername());
    }

    @Order(4)
    @DisplayName("성공 - kakaoId검색")
    @Test
    void findByKakaoId() {

        //when
        Optional<User> result = userRepository.findByKakaoId(22222L);

        //then
        assertNotEquals(Optional.empty(), result);
        assertEquals("카카오2", result.get().getNickname());
    }

    @Order(5)
    @DisplayName("결과없음 - username검색")
    @Test
    void emptyResult() {

        //when
        Optional<User> result = userRepository.findByUsername("nobody@nobody.kr");

        //then
        assertEquals(Optional.empty(), result);
    }

    @Order(6)
    @DisplayName("결과없음 - nickname검색")
    @Test
    void emptyResult2() {

        //when
        Optional<User> result = userRepository.findByNickname("없는닉네임");

        //then
        assertEquals(Optional.empty(), result);
    }

    @Order(7)
    @DisplayName("결과없음 - kakaoId검색")
    @Test
    void emptyResult3() {

        //when
        Optional<User> result = userRepository.findByKakaoId(00000L);

        //then
        assertEquals(Optional.empty(), result);
    }
}