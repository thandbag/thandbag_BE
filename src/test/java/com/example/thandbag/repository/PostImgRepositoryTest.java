package com.example.thandbag.repository;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.PostImg;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class PostImgRepositoryTest {

    @Autowired
    PostImgRepository postImgRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    User user;
    Post post1;
    PostImg postImg1;
    PostImg postImg2;

    @BeforeEach
    void setup() {
        // 유저 생성
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@test.kr",
                "테스트",
                "test1234!@",
                "INFJ"
        );
        this.user = new User(signupRequestDto);

        // 유저 저장
        userRepository.save(user);

        // Post1 생성
        this.post1 = Post.builder()
                .title("Post1")
                .content("Post Content1")
                .closed(false)
                .share(true)
                .user(user)
                .category(Category.SOCIAL)
                .totalHitCount(0)
                .build();

        // DB 저장
        postRepository.save(post1);

        // PostImg 생성
        this.postImg1 = PostImg.builder()
                .postImgUrl("postImgUrl1.jpg")
                .post(post1)
                .build();

        this.postImg2 = PostImg.builder()
                .postImgUrl("postImgUrl2.jpg")
                .post(post1)
                .build();

        postImgRepository.save(postImg1);
        postImgRepository.save(postImg2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색 ")
    @Test
    void saveAndFindAll() {

        //when
        List<PostImg> result = postImgRepository.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals("postImgUrl1.jpg", result.get(0).getPostImgUrl());
        assertEquals("Post1", result.get(1).getPost().getTitle());
        assertEquals("테스트", result.get(1).getPost().getUser().getNickname());
    }

    @Order(2)
    @DisplayName("성공 - postId검색")
    @Test
    void findAllByPostId() {

        //when
        List<PostImg> result = postImgRepository.findAllByPostId(post1.getId());

        //then
        assertEquals(2, result.size());
        assertEquals("postImgUrl1.jpg", result.get(0).getPostImgUrl());
        assertEquals("Post1", result.get(1).getPost().getTitle());
        assertEquals("테스트", result.get(1).getPost().getUser().getNickname());
    }
}