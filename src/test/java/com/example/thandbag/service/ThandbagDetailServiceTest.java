package com.example.thandbag.service;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.post.BestUserDto;
import com.example.thandbag.dto.post.HitCountDto;
import com.example.thandbag.dto.post.ThandbagRequestDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
import com.example.thandbag.model.*;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ThandbagDetailServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    LvImgRepository lvImgRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    AlarmRepository alarmRepository;
    @Mock
    RedisTemplate redisTemplate;
    @Mock
    ChannelTopic channelTopic;

    ThandbagDetailService thandbagDetailService;

    private Long id;
    private String title;
    private String content;
    private Boolean closed;
    private Boolean share;
    private User user;
    private Category category;
    private List<Comment> commentList;
    private ThandbagRequestDto thandbagRequestDto;
    private Post post;
    private List<PostImg> postImgList;
    private Comment comment;

    @BeforeEach
    void setup() {
        title = "테스트";
        content = "테스트내용";
        closed = false;
        share = true;
        user = User.builder()
                .id(1L)
                .username("asb@abc.abc")
                .password("1234")
                .nickname("test")
                .mbti("INTP")
                .totalCount(1)
                .level(1)
                .profileImg(new ProfileImg(1L, "asdf"))
                .auth(Auth.USER).build();
        category = Category.LOVE;
        thandbagDetailService = new ThandbagDetailService(
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                postRepository,
                userRepository,
                lvImgRepository,
                commentLikeRepository,
                alarmRepository,
                redisTemplate,
                channelTopic
                );
        commentList = new ArrayList<>();
        thandbagRequestDto = new ThandbagRequestDto(
                title,
                content,
                null,
                "LOVE",
                share);
        postImgList = new ArrayList<>();

        post = Post.builder()
                .id(1L)
                .title(thandbagRequestDto.getTitle())
                .category(category)
                .closed(thandbagRequestDto.isShare())
                .content(thandbagRequestDto.getContent())
                .imgList(postImgList)
                .share(thandbagRequestDto.isShare())
                .user(user)
                .commentList(commentList)
                .totalHitCount(0)
                .build();
        post.setCreatedAt(LocalDateTime.now());

        comment = Comment.builder()
                .comment(content)
                .likedByWriter(false)
                .user(user)
                .post(post)
                .id(1L)
                .commentLikeList(new ArrayList<>())
                .build();
    }


    @DisplayName("생드백 상세보기")
    @Test
    @Order(1)
    void getOneThandbag() {

        post.getCommentList().add(comment);
        comment.setCreatedAt(LocalDateTime.now());

        /* given */
        given(postRepository.findById(post.getId()))
                .willReturn(Optional.of(post));
        given(lvImgRepository.findByTitleAndLevel(anyString(),anyInt()))
                .willReturn(new LvImg("asdf", "asdf", 1));

        /* when */
        thandbagDetailService = new ThandbagDetailService(
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                postRepository,
                userRepository,
                lvImgRepository,
                commentLikeRepository,
                alarmRepository,
                redisTemplate,
                channelTopic
        );
        ThandbagResponseDto thandbagResponseDto =
                thandbagDetailService.getOneThandbag(post.getId(), user);

        /* then */
        assertNotNull(thandbagResponseDto);
        assertEquals(thandbagResponseDto.getUserId(), 1);
        assertEquals(1, thandbagResponseDto.getComments().size());
        assertEquals("테스트", thandbagResponseDto.getTitle());

    }

    @DisplayName("생드백 삭제")
    @Test
    @Order(2)
    void removeThandbag() {
        /* given */
        Long postId = 1L;

        /* when */
        thandbagDetailService.removeThandbag(postId, user);

        /* then */
        assertEquals(0, user.getTotalCount());
        /* 생드백삭제 후, 해당 포스트에 대한 알림이 삭제되는지 확인 */
        then(alarmRepository)
                .should(times(1))
                .deleteAllByPostId(postId);
    }

    @DisplayName("생드백 떠트리기")
    @Test
    @Order(3)
    void completeThandbag() {

        post.getCommentList().add(comment);
        comment.setCreatedAt(LocalDateTime.now());


        /* given */
        HitCountDto hitCountDto = new HitCountDto(0, 10);

        User user1 = User.builder()
                .id(2L)
                .username("hh@hoho.haha")
                .password("1234")
                .nickname("test2")
                .mbti("ESFJ")
                .totalCount(1)
                .level(1)
                .profileImg(new ProfileImg(2L, "asdf"))
                .auth(Auth.USER).build();

        Comment comment2 = Comment.builder()
                .comment("테스트코멘트2")
                .likedByWriter(true)
                .user(user1)
                .post(post)
                .id(2L)
                .build();

        post.getCommentList().add(comment2);
        comment2.setCreatedAt(LocalDateTime.now());

        given(postRepository.findById(post.getId()))
                .willReturn(Optional.of(post));

        given(channelTopic.getTopic())
                .willReturn("aside");

        /* when */
        thandbagDetailService = new ThandbagDetailService(
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic),
                postRepository,
                userRepository,
                lvImgRepository,
                commentLikeRepository,
                alarmRepository,
                redisTemplate,
                channelTopic
        );
        List<BestUserDto> bestUserDtoList =
                thandbagDetailService.completeThandbag(post.getId(),hitCountDto);

        /* then */
        assertEquals(1, bestUserDtoList.size());
        assertEquals("test2", bestUserDtoList.get(0).getNickname());
        assertEquals("ESFJ", bestUserDtoList.get(0).getMbti());
    }
}