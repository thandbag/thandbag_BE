package com.example.thandbag.controller;

import com.example.thandbag.TestConfig;
import com.example.thandbag.dto.chat.ChatMessageDto;
import com.example.thandbag.dto.chat.ChatMyRoomListResponseDto;
import com.example.thandbag.dto.chat.chatroom.ChatRoomDto;
import com.example.thandbag.dto.chat.chatroom.CreateRoomRequestDto;
import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatRoomControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LvImgRepository lvImgRepository;
    @Autowired
    private ProfileImgRepository profileImgRepository;

    private HttpHeaders headers;
    private final ObjectMapper mapper = new ObjectMapper();

    private String token = "";
    private String chatRoomId = "";

    private SignupRequestDto user1 = SignupRequestDto.builder()
            .username("xxx@naver.com")
            .nickname("didl12")
            .password("tjds1234!")
            .mbti("INFJ")
            .build();

    private SignupRequestDto user2 = SignupRequestDto.builder()
            .username("aaa@naver.com")
            .nickname("hhihi")
            .password("tjds1234!")
            .mbti("INFP")
            .build();

    private LoginRequestDto user1Login = LoginRequestDto.builder()
            .username("xxx@naver.com")
            .password("tjds1234!")
            .build();

    private LoginRequestDto user2Login = LoginRequestDto.builder()
            .username("aaa@naver.com")
            .password("tjds1234!")
            .build();

    @AfterAll
    public void cleanup() {
        Optional<User> user = userRepository.findByUsername("xxx@naver.com");
        Optional<User> user2 = userRepository.findByUsername("aaa@naver.com");
        chatRoomRepository.deleteById(chatRoomId);
        userRepository.deleteById(user.get().getId());
        userRepository.deleteById(user2.get().getId());
        assertEquals(Optional.empty(),
                    chatRoomRepository.findById(chatRoomId));
        assertEquals(Optional.empty(),
                    userRepository.findById(user.get().getId()));
        assertEquals(Optional.empty(),
                    userRepository.findById(user2.get().getId()));
    }

    @BeforeAll
    public void preSet() {
        TestConfig.initialQuery(lvImgRepository, profileImgRepository);
    }

    @BeforeEach
    public void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(1)
    @DisplayName("회원 가입")
    void test1() throws JsonProcessingException {
        /* given */
        String requestBody = mapper.writeValueAsString(user1);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        /* when */
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/user/signup",
                request,
                String.class);

        /* then */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("회원가입 성공", response.getBody());
    }

    @Test
    @Order(2)
    @DisplayName("회원 가입 2")
    void test2() throws JsonProcessingException {
        /* given */
        String requestBody = mapper.writeValueAsString(user2);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        /* when */
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/user/signup",
                request,
                String.class);

        /* then */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("회원가입 성공", response.getBody());
    }


    @Test
    @Order(3)
    @DisplayName("로그인, JWT 토큰 받기")
    void test3() throws JsonProcessingException {
        /* given */
        String requestBody = mapper.writeValueAsString(user2Login);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        /* when */
        ResponseEntity<LoginResultDto> response = restTemplate.postForEntity(
                "/api/user/login",
                request,
                LoginResultDto.class);

        /* then */
        token = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertNotEquals("", token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Nested
    @DisplayName("채팅룸 테스트 - ChatRoomController")
    class ChatRoom {
        @Test
        @Order(1)
        @DisplayName("채팅룸 생성 1")
        void test1() throws JsonProcessingException {
            /* given */
            Optional<User> user = userRepository
                    .findByUsername("xxx@naver.com");
            Optional<User> user2 = userRepository
                    .findByUsername("aaa@naver.com");

            CreateRoomRequestDto createRoomRequestDto =
                    new CreateRoomRequestDto(user.get().getId(),
                                            user2.get().getId());

            String requestBody = mapper
                    .writeValueAsString(createRoomRequestDto);

            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            /* when */
            ResponseEntity<ChatRoomDto> response = restTemplate.postForEntity(
                    "/chat/room",
                    request,
                    ChatRoomDto.class);

            /* then */
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            chatRoomId = response.getBody().getRoomId();
        }

        @Test
        @Order(2)
        @DisplayName("채팅방 조회 1")
        void test2() throws JsonProcessingException {
            /* given */
            Optional<User> user = userRepository
                    .findByUsername("xxx@naver.com");
            Optional<User> user2 = userRepository
                    .findByUsername("aaa@naver.com");

            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(headers);

            /* when */
            ResponseEntity<Object> response = restTemplate.postForEntity(
                    "/chat/room/enter/" + chatRoomId,
                    request,
                    Object.class);

            /* then */
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

        }

        @Test
        @Order(3)
        @DisplayName("내가 참가한 모든 채팅방 목록 1")
        void test3() throws JsonProcessingException {
            /* given */
            Optional<User> user = userRepository
                    .findByUsername("xxx@naver.com");
            Optional<User> user2 = userRepository
                    .findByUsername("aaa@naver.com");
            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(headers);

            /* when */
            ResponseEntity<String> response = restTemplate.exchange(
                    "/chat/myRoomList",
                        HttpMethod.GET,
                        request,
                        String.class);

            /* then */
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }


        @Test
        @Order(4)
        @DisplayName("getUserInfo()")
        void test4() throws JsonProcessingException {
            /* given */
            Optional<User> user = userRepository
                    .findByUsername("xxx@naver.com");
            Optional<User> user2 = userRepository
                    .findByUsername("aaa@naver.com");
            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(headers);

            /* when */
            ResponseEntity<Object> response = restTemplate.exchange(
                    "/chat/user",
                    HttpMethod.GET,
                    request,
                    Object.class);

            /* then */
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}