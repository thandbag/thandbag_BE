package com.example.thandbag.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
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
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    PostImgRepository postImgRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    LvImgRepository lvImgRepository;
    @Mock
    AlarmRepository alarmRepository;
    RedisTemplate redisTemplate;
    ChannelTopic channelTopic;

    private String title;
    private String content;
    private Boolean closed;
    private Boolean share;
    private User user;
    private Category category;
    private MainService mainService;

    @BeforeEach
    void setup() {
        title = "제목1";
        content = "내용1";
        closed = false;
        share = true;
        user = User.builder()
                .id(1L)
                .username("asb@abc.abc")
                .password("1234")
                .nickname("nickname")
                .mbti("INTP")
                .totalCount(0)
                .level(1)
                .profileImg(new ProfileImg(1L, "asdf"))
                .auth(Auth.USER).build();
        category = Category.LOVE;
        mainService = new MainService(postRepository,
                postImgRepository,
                userRepository,
                lvImgRepository,
                new ImageService(new AmazonS3Client()),
                alarmRepository,
                redisTemplate,
                channelTopic);

    }

    @Test
    @DisplayName("생드백 만들기(img 없이, 공유 o)")
    @Order(1)
    void createThandbag() throws IOException {

        /* given */
        ThandbagRequestDto thandbagRequestDto =
                new ThandbagRequestDto(
                        title,
                        content,
                        null,
                        "LOVE",
                        share);

        List<PostImg> postImgList = new ArrayList<>();
        Post post = Post.builder()
                .id(1L)
                .title(thandbagRequestDto.getTitle())
                .category(category)
                .closed(thandbagRequestDto.isShare())
                .content(thandbagRequestDto.getContent())
                .imgList(postImgList)
                .share(thandbagRequestDto.isShare())
                .user(user)
                .build();
        post.setCreatedAt(LocalDateTime.now());

        /* when */
        when(postRepository.save(any())).thenReturn(post);
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(userRepository.save(any())).thenReturn(user);
        when(lvImgRepository.findByTitleAndLevel(anyString(), anyInt()))
                .thenReturn(new LvImg("aaa", "aaa", 1));

        ThandbagResponseDto thandbagResponseDto =
                mainService.createThandbag(thandbagRequestDto, user);

        /* then */
        assertEquals("LOVE", thandbagResponseDto.getCategory());
        assertNotNull(title);
        assertFalse(closed);
        assertTrue(share);
        assertNotNull(content);
    }


    @Test
    @Order(2)
    @DisplayName("생드백 및 회원 검색 - 검색 키워드 매칭 o")
    void searchThandbags() {

        /* given */
        ThandbagRequestDto thandbagRequestDto =
                new ThandbagRequestDto(
                        title,
                        content,
                        null,
                        "LOVE",
                        share);

        List<PostImg> postImgList = new ArrayList<>();
        Post post = Post.builder()
                .id(1L)
                .title(thandbagRequestDto.getTitle())
                .category(category)
                .closed(thandbagRequestDto.isShare())
                .content(thandbagRequestDto.getContent())
                .imgList(postImgList)
                .share(thandbagRequestDto.isShare())
                .user(user)
                .commentList(new ArrayList<>())
                .build();
        post.setCreatedAt(LocalDateTime.now());

        List<Post> allposts = new ArrayList<>();
        allposts.add(post);

        Pageable sortedByModifiedAtDesc =
                PageRequest.of(0, 1, Sort.by("modifiedAt")
                        .descending());
        int start = (int) sortedByModifiedAtDesc.getOffset();
        int end = Math.min((start + sortedByModifiedAtDesc.getPageSize()),
                            allposts.size());
        Page<Post> page = new PageImpl<>(allposts.subList(start, end),
                                        sortedByModifiedAtDesc, allposts.size());

        /* when */
        when(postRepository
                .findAllByShareTrueAndContainsKeywordForSearch(anyString(), any()))
                .thenReturn(page);

        /* then */
        assertEquals(1,
                mainService.searchThandbags("내용", 0, 1)
                        .size());
    }
}






