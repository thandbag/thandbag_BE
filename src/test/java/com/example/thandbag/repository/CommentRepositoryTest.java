package com.example.thandbag.repository;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    User user;
    Post post;
    Comment comment1;
    Comment comment2;

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
        this.post = Post.builder()
                .title("Post1")
                .content("Post Content1")
                .closed(false)
                .share(true)
                .user(user)
                .category(Category.SOCIAL)
                .totalHitCount(0)
                .build();

        // DB 저장
        postRepository.save(post);

        this.comment1 = Comment.builder()
                .comment("코멘트1")
                .likedByWriter(true)
                .user(user)
                .post(post)
                .build();

        this.comment2 = Comment.builder()
                .comment("코멘트2")
                .likedByWriter(false)
                .user(user)
                .post(post)
                .build();

        // DB에 저장
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        //when
        List<Comment> result = commentRepository.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals("코멘트1", result.get(0).getComment());
        assertFalse(result.get(1).getLikedByWriter());
    }

}