package com.example.thandbag.controller;

import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.mypage.MyPageResponseDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateRequestDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateResponseDto;
import com.example.thandbag.dto.post.ThandbagRequestDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MyPageControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private Long postId;

    private HttpHeaders headers;
    private final ObjectMapper mapper = new ObjectMapper();

    private String token = "";

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
    @DisplayName("마이페이지 테스트 - Mypage controller")
    class MypageEdit {
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
        @DisplayName("회원정보 수정 1")
        void test2() throws IOException {

            //given
            ProfileUpdateRequestDto profileUpdateRequestDto = ProfileUpdateRequestDto
                    .builder()
                    .nickname("바껴라")
                    .mbti("INFJ")
                    .build();

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

            headers.set("Content-Type", "multipart/form-data");
            headers.set("Authorization", token);

            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "image.image",
                    "image/png",
                    new FileInputStream("src/test/resources/templates/testImg/KakaoTalk_Photo_2021-05-10-00-14-49.jpeg")
            );

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() throws IllegalStateException {
                    return file.getOriginalFilename();
                }
            };
            map.set("file", resource);
            map.set("updateDto", profileUpdateRequestDto);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(map, headers);

            //when
            ResponseEntity<ProfileUpdateResponseDto> response = restTemplate.postForEntity(
                    "/mypage/profile",
                    requestEntity,
                    ProfileUpdateResponseDto.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("INFJ", response.getBody().getMbti());
            assertEquals("바껴라", response.getBody().getNickname());
        }

        @Test
        @Order(3)
        @DisplayName("내 생드백 불러오기")
        void test3() throws JsonProcessingException {
            //when
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<MyPageResponseDto> response = restTemplate.exchange(
                    "/api/myThandbag?pageNo=0&sizeNo=2", HttpMethod.GET, request, MyPageResponseDto.class);

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().getMyPostList().size());
            assertEquals(1, response.getBody().getLevel());
        }
    }
}