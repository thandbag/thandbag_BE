package com.example.thandbag.controller;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.TestConfig;
import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Alarm;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.AlarmRepository;
import com.example.thandbag.repository.LvImgRepository;
import com.example.thandbag.repository.ProfileImgRepository;
import com.example.thandbag.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlarmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private LvImgRepository lvImgRepository;
    @Autowired
    private ProfileImgRepository profileImgRepository;

    private HttpHeaders headers;
    private final ObjectMapper mapper = new ObjectMapper();

    private String token = "";

    private Alarm alarm;
    private Alarm alarm2;
    private Long alarmId;
    private Long alarm2Id;


    private SignupRequestDto user1 = SignupRequestDto.builder()
            .username("xxx@naver.com")
            .nickname("didl12")
            .password("tjds1234!")
            .mbti("INFJ")
            .build();

    private LoginRequestDto user1Login = LoginRequestDto.builder()
            .username("xxx@naver.com")
            .password("tjds1234!")
            .build();

    @AfterAll
    public void cleanup() {
        Optional<User> user = userRepository.findByUsername("xxx@naver.com");
        userRepository.deleteById(user.get().getId());
        alarmRepository.deleteById(alarmId);
        alarmRepository.deleteById(alarm2Id);
        assertEquals(Optional.empty(), userRepository.findById(user.get().getId()));
        assertEquals(Optional.empty(), alarmRepository.findById(alarmId));
        assertEquals(Optional.empty(), alarmRepository.findById(alarm2Id));
    }

    @BeforeAll
    public void preSet() {
        TestConfig.initialQuery(lvImgRepository, profileImgRepository);
    }

    @BeforeEach
    public void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        alarm = Alarm.builder()
                .type(AlarmType.REPLY)
                .alarmMessage("hihi")
                .isRead(false)
                .postId(2000L)
                .userId(2000L)
                .pubId(2000L)
                .build();
        alarm2 = Alarm.builder()
                .type(AlarmType.REPLY)
                .alarmMessage("hellohello")
                .isRead(true)
                .postId(3000L)
                .userId(3000L)
                .pubId(3000L)
                .build();
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
    @DisplayName("로그인, JWT 토큰 받기")
    void test2() throws JsonProcessingException {
        /* given */
        String requestBody = mapper.writeValueAsString(user1Login);
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
    @DisplayName("알람기능 - alarm controller")
    class AlarmTest {
        @Test
        @Order(1)
        @DisplayName("알람 리스트 1 - reply")
        void test1() throws JsonProcessingException {

            /* given */
            Optional<User> user = userRepository
                    .findByUsername("xxx@naver.com");

            alarm.setUserId(user.get().getId());
            alarm2.setUserId(user.get().getId());
            alarm = alarmRepository.save(alarm);
            alarm2 = alarmRepository.save(alarm2);
            alarmId = alarm.getId();
            alarm2Id = alarm2.getId();

            /* when */
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<Object> response = restTemplate.exchange(
                    "/api/alarm?page=0&size=2",
                    HttpMethod.GET,
                    request,
                    Object.class
            );

            /* then */
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<AlarmResponseDto> temp =
                    (List<AlarmResponseDto>) response.getBody();
            assertEquals(2, temp.size());
        }

        @Test
        @Order(2)
        @DisplayName("알람 읽음 1")
        void test2() throws JsonProcessingException {

            /* given */
            Optional<User> user = userRepository
                    .findByUsername("xxx@naver.com");

            alarm.setUserId(user.get().getId());
            alarm2.setUserId(user.get().getId());

            alarm = alarmRepository.save(alarm);
            alarm2 = alarmRepository.save(alarm2);

            alarmId = alarm.getId();
            alarm2Id = alarm2.getId();

            /* when */
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<AlarmResponseDto> response =
                    restTemplate.postForEntity(
                            "/api/alarm/" + alarmId,
                            request,
                            AlarmResponseDto.class
            );

            /* then */
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().getIsRead());
        }
    }
}