package com.example.thandbag.service;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.comment.PostCommentDto;
import com.example.thandbag.dto.comment.ShowCommentDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
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
    @Mock
    ChatRoomRepository chatRoomRepository;

    RedisTemplate redisTemplate;
    ChannelTopic channelTopic;
    CommentService commentService;

    private Boolean likedByWriter;
    private User user1;
    private User user2;
    private Post post;
    private Comment comment1;

    @BeforeEach
    void setup() {
        likedByWriter = false;

        ProfileImg profileImg = new ProfileImg(
                1L,
                "naver.com"
        );

        user1 = User.builder()
                .id(1L)
                .username("asb@abc.abc")
                .password("1234")
                .nickname("유저1")
                .mbti("INTP")
                .totalCount(1)
                .level(1)
                .profileImg(profileImg)
                .auth(Auth.USER).build();

        user2 = User.builder()
                .id(2L)
                .username("asb2@abc.abc")
                .password("1234")
                .nickname("유저2")
                .mbti("INTP")
                .totalCount(1)
                .level(2)
                .profileImg(profileImg)
                .auth(Auth.USER).build();

        post = Post.builder()
                .id(1L)
                .title("개노잼")
                .category(Category.SOCIAL)
                .closed(false)
                .content("이렇게 하는게 맞나")
                .imgList(new ArrayList<>())
                .share(true)
                .user(user1)
                .commentList(new ArrayList<>())
                .commentCount(0)
                .build();

        comment1 = Comment.builder()
                .id(1L)
                .comment("코멘트1")
                .likedByWriter(false)
                .user(user2)
                .post(post)
                .commentLikeList(new ArrayList<>())
                .build();

        commentService = new CommentService(
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                commentRepository,
                postRepository,
                userRepository,
                commentLikeRepository
                );
    }

    @DisplayName("댓글 작성")
    @Test
    @Order(1)
    void postComment() {
        /* given */
        UserDetailsImpl userDetails = new UserDetailsImpl(user1);

        given(postRepository.getById(anyLong())).willReturn(post);
        given(commentRepository.save(any(Comment.class))).willReturn(comment1);

        String content = comment1.getComment();
        comment1.setCreatedAt(LocalDateTime.now());

        Long postId = post.getId();

        given(userRepository
                .getById(postRepository.getById(postId).getUser().getId()))
                .willReturn(user1);

        given(postRepository.getById(1L)).willReturn(post);

        /* when */
        PostCommentDto result =
                commentService.postComment(postId, content, userDetails);

        /* then */
        assertEquals(content, result.getComment());
        assertEquals(user1.getLevel(), result.getLevel());
        assertEquals(user1.getNickname(), result.getNickname());
        assertEquals(user1.getProfileImg().getProfileImgUrl(),
                result.getProfileImgUrl());
        assertFalse(result.isCurrentUserlike());

    }

    @DisplayName("댓글 삭제")
    @Test
    @Order(2)
    void deleteComment() {
        /* given */
        Long commentId = comment1.getId();
        when(commentRepository.getById(1L)).thenReturn(comment1);

        /* when */
        commentService.deleteComment(commentId, user1);

        /* then */
        assertEquals(0, user1.getTotalCount());
        then(commentRepository)
                .should(times(1))
                .deleteById(commentId);
    }

    @DisplayName("좋아요 안했을 경우 +1")
    @Test
    @Order(3)
    void likeComment() {
        /* given */
        Long commentId = comment1.getId();
        UserDetailsImpl userDetails = new UserDetailsImpl(user2);
        comment1.setCreatedAt(LocalDateTime.now());

        List<CommentLike> commentLikeList = new ArrayList<>();
        CommentLike commentLike1 = CommentLike.builder()
                        .userId(user2.getId())
                        .comment(comment1)
                        .build();
        commentLikeList.add(commentLike1);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment1));
        given(commentLikeRepository
                .findByUserIdAndComment(anyLong(), any(Comment.class)))
                .willReturn(null);
        given(commentLikeRepository.findAllByComment(any(Comment.class)))
                .willReturn(commentLikeList);

        /* when */
        ShowCommentDto result =
                commentService.likeComment(commentId, userDetails);

        /* then */
        assertEquals(comment1.getComment(), result.getComment());
        assertEquals(1, result.getLike());
    }

    @DisplayName("이미 좋아요 한 경우 -1")
    @Test
    @Order(4)
    void likeComment2() {
        /* given */
        Long commentId = comment1.getId();
        UserDetailsImpl userDetails = new UserDetailsImpl(user2);
        comment1.setCreatedAt(LocalDateTime.now());

        List<CommentLike> commentLikeList = new ArrayList<>();
        CommentLike commentLike1 = CommentLike.builder()
                .userId(user2.getId())
                .comment(comment1)
                .build();
        commentLikeList.add(commentLike1);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment1));
        given(commentLikeRepository
                .findByUserIdAndComment(anyLong(), any(Comment.class)))
                .willReturn(commentLike1);
        given(commentLikeRepository.findAllByComment(any(Comment.class)))
                .willReturn(commentLikeList);

        commentLikeList.remove(commentLike1);

        /* when */
        ShowCommentDto result =
                commentService.likeComment(commentId, userDetails);

        /* then */
        assertEquals(comment1.getComment(), result.getComment());
        assertEquals(0, result.getLike());
    }
}