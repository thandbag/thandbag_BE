package com.example.thandbag.repository;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class CommentLikeRepositoryTest {

    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;


    User user1;
    User user2;
    Post post;
    Comment comment1;
    Comment comment2;
    Comment comment3;
    CommentLike commentLike1;
    CommentLike commentLike2;

    @BeforeEach
    void setup() {
        /* 유저 생성 */
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

        /* DB 저장 */
        userRepository.save(user1);
        userRepository.save(user2);

        /* Post1 생성 */
        this.post = Post.builder()
                .title("Post1")
                .content("Post Content1")
                .closed(false)
                .share(true)
                .user(user1)
                .category(Category.SOCIAL)
                .totalHitCount(0)
                .build();

        /* DB 저장 */
        postRepository.save(post);

        /* 코멘트 생성 */
        this.comment1 = Comment.builder()
                .comment("코멘트1")
                .user(user2)
                .post(post)
                .build();

        this.comment2 = Comment.builder()
                .comment("코멘트2")
                .user(user1)
                .post(post)
                .build();

        this.comment3 = Comment.builder()
                .comment("코멘트3")
                .user(user1)
                .post(post)
                .build();

        /* DB 저장 */
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        /* CommentLike 생성 */
        commentLike1 = CommentLike.builder()
                .userId(user1.getId())
                .comment(comment1)
                .build();

        commentLike2 = CommentLike.builder()
                .userId(user2.getId())
                .comment(comment2)
                .build();

        /* DB 저장 */
        commentLikeRepository.save(commentLike1);
        commentLikeRepository.save(commentLike2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        /* when */
        List<CommentLike> result = commentLikeRepository.findAll();

        /* then */
        assertEquals(2, result.size());
        assertEquals("코멘트1",
                result.get(0)
                        .getComment()
                        .getComment());
        assertEquals("테스트2",
                result.get(0)
                        .getComment()
                        .getUser()
                        .getNickname());
        assertEquals("Post1",
                result.get(1)
                        .getComment()
                        .getPost()
                        .getTitle());
    }

    @Order(2)
    @DisplayName("성공 - 유저ID+코멘트검색")
    @Test
    void findByUserIdAndComment() {

        /* when */
        CommentLike result = commentLikeRepository
                .findByUserIdAndComment(user1.getId(), comment1);

        /* then */
        assertNotNull(result);
        assertEquals(comment1, result.getComment());
    }

    @Order(3)
    @DisplayName("성공 - 코멘트검색")
    @Test
    void findAllByComment() {

        /* when */
        List<CommentLike> result = commentLikeRepository
                .findAllByComment(comment1);

        /* then */
        assertEquals(1, result.size());
        assertEquals("테스트2",
                result.get(0)
                        .getComment()
                        .getUser()
                        .getNickname());
    }

    @Order(4)
    @DisplayName("성공 - 코멘트+유저ID검색(boolean)")
    @Test
    void existsByCommentAndUserId() {

        /* when */
        boolean result = commentLikeRepository
                .existsByCommentAndUserId(comment1, user1.getId());

        /* then */
        assertTrue(result);
    }

    @Order(5)
    @DisplayName("결과없음 - 유저ID+코멘트검색")
    @Test
    void findByUserIdAndComment2() {

        /* when */
        CommentLike result = commentLikeRepository
                .findByUserIdAndComment(user1.getId(), comment2);

        /* then */
        assertNull(result);
    }

    @Order(6)
    @DisplayName("결과없음 - 코멘트검색")
    @Test
    void findAllByComment2() {

        /* when */
        List<CommentLike> result = commentLikeRepository
                .findAllByComment(comment3);

        /* then */
        assertEquals(0, result.size());
    }

    @Order(7)
    @DisplayName("결과없음 - 코멘트+유저ID검색(boolean)")
    @Test
    void existsByCommentAndUserId2() {

        /* when */
        boolean result = commentLikeRepository
                .existsByCommentAndUserId(comment3, user1.getId());

        /* then */
        assertFalse(result);
    }
}