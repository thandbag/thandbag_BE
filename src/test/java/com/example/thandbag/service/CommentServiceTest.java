package com.example.thandbag.service;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    AlarmRepository alarmRepository;
    RedisTemplate redisTemplate;
    ChannelTopic channelTopic;
    CommentService commentService;

    private String comment;
    private Boolean likedByWriter;
    private User user;
    private Post post;

    @BeforeEach
    void setup() {
        comment = "레알 테케 쓰는거 개노잼";
        likedByWriter = false;
        user = User.builder()
                .id(1L)
                .username("asb@abc.abc")
                .password("1234")
                .nickname("nojam")
                .mbti("INTP")
                .totalCount(1)
                .level(1)
                .auth(Auth.USER).build();
        post = Post.builder()
                .id(1L)
                .title("개노잼")
                .category(Category.SOCIAL)
                .closed(false)
                .content("이렇게 하는게 맞나")
                .imgList(new ArrayList<>())
                .share(true)
                .user(user)
                .commentList(new ArrayList<>())
                .build();
        commentService = new CommentService(commentRepository, postRepository,
                userRepository, commentLikeRepository, alarmRepository, redisTemplate, channelTopic);
    }

    @DisplayName("댓글 작성")
    @Test
    @Order(1)
    void postComment() {

    }

    @DisplayName("댓글 삭제")
    @Test
    @Order(2)
    void deleteComment() {
        commentService.deleteComment(1, user);
        assertEquals(0, user.getTotalCount());
    }

    @DisplayName("좋아요")
    @Test
    @Order(3)
    void likeComment() {

    }
}