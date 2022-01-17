package com.example.thandbag.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.post.ThandbagRequestDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import javax.persistence.*;

import java.nio.channels.Channel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private Long id;
    private String title;
    private String content;
    private Boolean closed;
    private Boolean share;
    private User user;
    private Category category;
    private List<Comment> commentList;
    private MainService mainService;

    @BeforeEach
    void setup() {
        title = "노잼";
        content = "레알 테케 쓰는거 개노잼";
        closed = false;
        share = true;
        user = User.builder()
                .id(1L)
                .username("asb@abc.abc")
                .password("1234")
                .nickname("nojam")
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

//    @Test
//    @DisplayName("샌드백 만들기(img 없이, 공유 o)")
//    @Order(1)
//    void createThandbag() {
//        ThandbagRequestDto thandbagRequestDto = new ThandbagRequestDto(title, content, null, "LOVE", share);
//        List<PostImg> postImgList = new ArrayList<>();
//        Post post = Post.builder()
//                .id(1L)
//                .title(thandbagRequestDto.getTitle())
//                .category(category)
//                .closed(thandbagRequestDto.isShare())
//                .content(thandbagRequestDto.getContent())
//                .imgList(postImgList)
//                .share(thandbagRequestDto.isShare())
//                .user(user)
//                .build();
////        when(postRepository.save(post))
////                .thenReturn(post);
////        when(postRepository.getById(post.getId()))
////                .thenReturn(post);
//
//        when(postRepository.getById(null)).thenReturn(post);
//        ThandbagResponseDto thandbagResponseDto = mainService.createThandbag(thandbagRequestDto, user);
//        assertEquals(category.getCategory(), thandbagResponseDto.getCategory());
//        assertNull(thandbagResponseDto.getCreatedAt());
//        assertNotNull(title);
//        assertFalse(closed);
//        assertTrue(share);
//        assertNotNull(content);
//    }

    @DisplayName("전체 게시글 조회")
    @Test
    @Order(2)
    void showAllThandbag() {


    }

//    @Nested
//    @Order(3)
//    @DisplayName("샌드백 및 회원 검색 - 검색 키워드 매칭 o")
//    class search {
//
//        @Test
//        @DisplayName("검색 키워드 매칭 o")
//        void searchThandbags() {
//            ThandbagRequestDto thandbagRequestDto = new ThandbagRequestDto(title, content, null, "LOVE", share);
//            List<PostImg> postImgList = new ArrayList<>();
//            Post post = Post.builder()
//                    .id(1L)
//                    .title(thandbagRequestDto.getTitle())
//                    .category(category)
//                    .closed(thandbagRequestDto.isShare())
//                    .content(thandbagRequestDto.getContent())
//                    .imgList(postImgList)
//                    .share(thandbagRequestDto.isShare())
//                    .user(user)
//                    .commentList(new ArrayList<>())
//                    .build();
//            post.setCreatedAt(LocalDateTime.now());
//            List<Post> allposts = new ArrayList<>();
//            allposts.add(post);
//            when(postRepository.findAllByShareTrueOrderByCreatedAtDesc())
//                    .thenReturn(allposts);
//            when(userRepository.getById(1L))
//                    .thenReturn(post.getUser());
//            assertEquals(1, mainService.searchThandbags("노잼", 0, 1).size());
//        }
//
//        @DisplayName("검색 키워드 매칭 x")
//        @Test
//        void searchThandbags2() {
//            ThandbagRequestDto thandbagRequestDto = new ThandbagRequestDto(title, content, null, "LOVE", share);
//            List<PostImg> postImgList = new ArrayList<>();
//            Post post = Post.builder()
//                    .id(1L)
//                    .title(thandbagRequestDto.getTitle())
//                    .category(category)
//                    .closed(thandbagRequestDto.isShare())
//                    .content(thandbagRequestDto.getContent())
//                    .imgList(postImgList)
//                    .share(thandbagRequestDto.isShare())
//                    .user(user)
//                    .commentList(new ArrayList<>())
//                    .build();
//            post.setCreatedAt(LocalDateTime.now());
//            List<Post> allposts = new ArrayList<>();
//            allposts.add(post);
//            when(postRepository.findAllByShareTrueOrderByCreatedAtDesc())
//                    .thenReturn(allposts);
//            when(userRepository.getById(1L))
//                    .thenReturn(post.getUser());
//            assertEquals(0, mainService.searchThandbags("PP", 0, 1).size());
//        }
//    }
}






