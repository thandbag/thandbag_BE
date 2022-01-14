package com.example.thandbag.IntegrationTest;

import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.post.HitCountDto;
import com.example.thandbag.dto.post.ThandbagRequestDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//mock안쓰고 직접 bean불러와 쓰는 integration test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThandbagDetailControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private HttpHeaders headers;
    private HttpHeaders headers2;
    private final ObjectMapper mapper = new ObjectMapper();

    private String token = "";
    private String token2 = "";
    private Long postId;
    private Long postId2;

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
        Optional<User> user = userRepository.findByUsername("aaa@naver.com");
        Optional<User> user2 = userRepository.findByUsername("xxx@naver.com");
        List<Post> postList = postRepository.findAllByUser(user2.get());
        postRepository.deleteById(postId);
        userRepository.deleteById(user.get().getId());
        userRepository.deleteById(user2.get().getId());
        assertEquals(Optional.empty(), userRepository.findById(user.get().getId()));
        assertEquals(Optional.empty(), userRepository.findById(user2.get().getId()));
        assertEquals(Optional.empty(), postRepository.findById(postList.get(0).getId()));
    }

    @BeforeEach
    public void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(1)
    @DisplayName("회원 가입")
    void test1() throws JsonProcessingException {
        // given
        String requestBody = mapper.writeValueAsString(user1);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/user/signup",
                request,
                String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("회원가입 성공", response.getBody());
    }

    @Test
    @Order(2)
    @DisplayName("회원 가입 2")
    void test2() throws JsonProcessingException {
        // given
        String requestBody = mapper.writeValueAsString(user2);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/user/signup",
                request,
                String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("회원가입 성공", response.getBody());
    }

    @Test
    @Order(3)
    @DisplayName("로그인, JWT 토큰 받기")
    void test3() throws JsonProcessingException {
        // given
        String requestBody = mapper.writeValueAsString(user2Login);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // when
        ResponseEntity<LoginResultDto> response = restTemplate.postForEntity(
                "/api/user/login",
                request,
                LoginResultDto.class);

        // then
        token = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertNotEquals("", token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    @DisplayName("로그인, JWT 토큰 받기 2")
    void test4() throws JsonProcessingException {
        // given
        String requestBody = mapper.writeValueAsString(user1Login);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // when
        ResponseEntity<LoginResultDto> response = restTemplate.postForEntity(
                "/api/user/login",
                request,
                LoginResultDto.class);

        // then
        token2 = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertNotEquals("", token2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Nested
    @DisplayName("생드백 테스트 - thandbagDetailController")
    class ThandbagDetail {
        @Test
        @Order(1)
        @DisplayName("생드백 만들기 1")
        void test1() throws JsonProcessingException {

            //given
            ThandbagRequestDto thandbagRequestDto = ThandbagRequestDto.builder()
                    .title("아디지겟다")
                    .content("기타쳣다")
                    .category("OTHERS")
                    .share(true)
                    .build();

            String requestBody = mapper.writeValueAsString(thandbagRequestDto);
            headers.set("Authorization", token2);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            //when
            ResponseEntity<ThandbagResponseDto> response = restTemplate.postForEntity(
                    "/api/newThandbag",
                    request,
                    ThandbagResponseDto.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Optional<User> user = userRepository.findByUsername("xxx@naver.com");
            System.out.println("user:" + user.get().getNickname());
            List<Post> postList = postRepository.findAllByUser(user.get());
            postId = postList.get(0).getId();
            System.out.println(postId);
            assertNotNull(postId);

        }

        @Test
        @Order(2)
        @DisplayName("생드백 만들기 2")
        void test2() throws JsonProcessingException {

            //given
            ThandbagRequestDto thandbagRequestDto = ThandbagRequestDto.builder()
                    .title("난 공부몬한다")
                    .content("공부하다 디짐")
                    .category("STUDY")
                    .share(true)
                    .build();

            String requestBody = mapper.writeValueAsString(thandbagRequestDto);
            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            //when
            ResponseEntity<ThandbagResponseDto> response = restTemplate.postForEntity(
                    "/api/newThandbag",
                    request,
                    ThandbagResponseDto.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Optional<User> user = userRepository.findByUsername("aaa@naver.com");
            List<Post> postList = postRepository.findAllByUser(user.get());
            postId2 = postList.get(0).getId();
            System.out.println(postId2);
            assertNotNull(postId2);

        }

        @Test
        @Order(3)
        @DisplayName("생드백 상세불러오기/삭제 1")
        void test3() throws JsonProcessingException {

            //when
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<Object> response2 = restTemplate.exchange(
                    "/api/thandbag/" + postId2, HttpMethod.GET, request, Object.class);

            //then
            assertEquals(HttpStatus.OK, response2.getStatusCode());

            //when
            ResponseEntity<String> response = restTemplate.exchange("/api/thandbag/" + postId2,
                    HttpMethod.DELETE, request, String.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());


            //when
            headers.set("Authorization", token);
            response2 = restTemplate.exchange(
                    "/api/thandbag/" + postId2, HttpMethod.GET, request, Object.class);

            //then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response2.getStatusCode());

        }

        @Test
        @Order(4)
        @DisplayName("생드백 때리기/때리기 페이지 이동 1")
        void test4() throws JsonProcessingException {


            HitCountDto hitCountDto1 = new HitCountDto(0, 10);
            HitCountDto hitCountDto2 = new HitCountDto(10, 20);
            HitCountDto hitCountDto3 = new HitCountDto(20, 30);
            HitCountDto hitCountDto4 = new HitCountDto(30, 33);
            HitCountDto hitCountDto5 = new HitCountDto(33, 33);

            //when
            String requestBody1 = mapper.writeValueAsString(hitCountDto1);
            String requestBody2 = mapper.writeValueAsString(hitCountDto2);
            String requestBody3 = mapper.writeValueAsString(hitCountDto3);
            String requestBody4 = mapper.writeValueAsString(hitCountDto4);
            String requestBody5 = mapper.writeValueAsString(hitCountDto5);
            headers.set("Authorization", token);
            headers2.set("Authorization", token2);
            HttpEntity request = new HttpEntity(requestBody5, headers);
            HttpEntity request1 = new HttpEntity(requestBody1, headers);
            HttpEntity request2 = new HttpEntity(requestBody2, headers2);
            HttpEntity request3 = new HttpEntity(requestBody3, headers2);
            HttpEntity request4 = new HttpEntity(requestBody4, headers);





            ResponseEntity<Object> response1 = restTemplate.exchange(
                    "/api/thandbag/punch/" + postId, HttpMethod.POST, request1, Object.class);
            ResponseEntity<Object> response2 = restTemplate.exchange(
                    "/api/thandbag/punch/" + postId, HttpMethod.POST, request2, Object.class);
            ResponseEntity<Object> response3 = restTemplate.exchange(
                    "/api/thandbag/punch/" + postId, HttpMethod.POST, request3, Object.class);
            ResponseEntity<Object> response4 = restTemplate.exchange(
                    "/api/thandbag/punch/" + postId, HttpMethod.POST, request4, Object.class);
            ResponseEntity<Object> response = restTemplate.exchange(
                    "/api/thandbag/punch/" + postId, HttpMethod.GET, request, Object.class);


            //System.out.println("total:" + postRepository.findById(2L).get().getTotalHitCount());
            //then
            assertEquals(HttpStatus.OK, response1.getStatusCode());
            assertEquals(HttpStatus.OK, response2.getStatusCode());
            assertEquals(HttpStatus.OK, response3.getStatusCode());
            assertEquals(HttpStatus.OK, response4.getStatusCode());
            assertEquals(33, postRepository.findById(postId).get().getTotalHitCount());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @Order(5)
        @DisplayName("생드백 떠뜨리기 1")
        void test5() throws JsonProcessingException {

            HitCountDto hitCountDto = new HitCountDto(33, 33);

            String requestBody = mapper.writeValueAsString(hitCountDto);
            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            //when
            ResponseEntity<Object> response = restTemplate.postForEntity(
                    "/api/thandbag?postId=" + postId,
                    request,
                    Object.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(postRepository.findById(postId).get().getClosed());
        }
    }
}

