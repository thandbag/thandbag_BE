package com.example.thandbag.controller;


import com.example.thandbag.TestConfig;
import com.example.thandbag.dto.comment.PostCommentDto;
import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.post.ThandbagRequestDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LvImgRepository lvImgRepository;
    @Autowired
    private ProfileImgRepository profileImgRepository;

    private HttpHeaders headers;
    private final ObjectMapper mapper = new ObjectMapper();

    private String token = "";

    private Long postId;

    private Long commentId;

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
        List<Post> postList = postRepository.findAllByUser(user.get());
        postRepository.deleteById(postId);
        userRepository.deleteById(user.get().getId());
        assertEquals(Optional.empty(), userRepository.findById(user.get().getId()));
        assertEquals(Optional.empty(), postRepository.findById(postList.get(0).getId()));
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
    @DisplayName("로그인, JWT 토큰 받기")
    void test2() throws JsonProcessingException {
        // given
        String requestBody = mapper.writeValueAsString(user1Login);
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

    @Nested
    @DisplayName("댓글 기능 - comment controller")
    class PostThandbag {
        @Test
        @Order(1)
        @DisplayName("생드백 만들기 1")
        void test1() throws JsonProcessingException {

            //given
            ThandbagRequestDto thandbagRequestDto = ThandbagRequestDto.builder()
                    .title("아아아")
                    .content("호호호")
                    .category("LOVE")
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
            Optional<User> user = userRepository.findByUsername("xxx@naver.com");
            List<Post> postList = postRepository.findAllByUser(user.get());
            postId = postList.get(0).getId();
            System.out.println(postId);
            assertNotNull(postId);


        }

        @Test
        @Order(2)
        @DisplayName("댓글 달기 1")
        void test2() throws JsonProcessingException {

            String requestBody = "야임마";
            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            //when
            ResponseEntity<PostCommentDto> response = restTemplate.postForEntity(
                    "/api/" + postId + "/newComment",
                    request,
                    PostCommentDto.class);

            //then
            PostCommentDto postCommentDto = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(requestBody, postCommentDto.getComment());
            commentId = postCommentDto.getCommentId();
            System.out.println(commentId);
        }

        @Test
        @Order(3)
        @DisplayName("댓글 좋아요/좋아요 취소 1")
        void test3() throws JsonProcessingException {

            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(headers);

            //when
            ResponseEntity<PostCommentDto> response = restTemplate.postForEntity(
                    "/api/" + commentId + "/like",
                    request,
                    PostCommentDto.class);

            //then
            Optional<User> user = userRepository.findByUsername("xxx@naver.com");
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(commentLikeRepository.existsByCommentAndUserId(commentRepository.getById(commentId), user.get().getId()));

            //when
            response = restTemplate.postForEntity(
                    "/api/" + commentId + "/like",
                    request,
                    PostCommentDto.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertFalse(commentLikeRepository.existsByCommentAndUserId(commentRepository.getById(commentId), user.get().getId()));
        }

        @Test
        @Order(4)
        @DisplayName("댓글 삭제 1")
        void test4() throws JsonProcessingException {

            //when
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<String> response = restTemplate.exchange("/api/uncomment/" + commentId,
                    HttpMethod.DELETE, request, String.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(Optional.empty(), commentRepository.findById(commentId));
        }

    }
}
