package com.example.thandbag.repository;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileImgRepository profileImgRepository;


    User user;
    Post post1;
    Post post2;

    @BeforeEach
    void setup() {
        /* 유저 생성 */
        ProfileImg profileImg = ProfileImg.builder()
                        .profileImgUrl("www.naver.com")
                        .build();
        profileImgRepository.save(profileImg);
        user = User.builder()
                    .username("test@test.kr")
                    .nickname("테스트")
                    .password("test1234!@")
                    .mbti("INFJ")
                    .level(1)
                    .auth(Auth.USER)
                    .totalCount(0)
                    .kakaoId(null)
                    .profileImg(profileImg)
                    .build();

        /* 유저 저장 */
        userRepository.save(user);

        /* CommentList 생성 */


        List<Comment> commentList = new ArrayList<>();


        /* Post1 생성 */
        this.post1 = Post.builder()
                .title("Post1")
                .content("Post Content1")
                .closed(false)
                .share(true)
                .user(user)
                .category(Category.SOCIAL)
                .totalHitCount(0)
                .build();

        /* Post2 생성 */
        this.post2 = Post.builder()
                .title("Post2")
                .content("Post Content2")
                .closed(false)
                .share(false)
                .user(user)
                .category(Category.LOVE)
                .totalHitCount(0)
                .build();

        /* DB 저장 */
        postRepository.save(post1);
        postRepository.save(post2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색 ")
    @Test
    void saveAndFindAll() {

        /* when */
        List<Post> result = postRepository.findAll();

        /* then */
        assertEquals(2, result.size());
        assertEquals("테스트", result.get(0).getUser().getNickname());
        assertEquals(Category.LOVE, result.get(1).getCategory());
    }

    @Order(2)
    @DisplayName("성공 - 유저검색 ")
    @Test
    void findAllByUser() {

        //when
        List<Post> result = postRepository.findAllByUser(user);

        //then
        assertEquals(2, result.size());
        assertEquals("테스트", result.get(0).getUser().getNickname());
    }

    @Order(3)
    @DisplayName("성공 - 유저검색(pageable) ")
    @Test
    void findAllByUserPageable() {
        //given
        int pageNo = 0;
        int sizeNo = 2;
        Pageable pageable = PageRequest.of(pageNo, sizeNo, Sort.by("createdAt").descending());
        post1.setCreatedAt(LocalDateTime.of(2022,01,13,21,14));
        post2.setCreatedAt(LocalDateTime.of(2022,01,14,21,14));

        //when
        Page<Post> result = postRepository.
                findAllByUserOrderByCreatedAtDesc(user, pageable);

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals(14,
                result.get().findFirst().get().getCreatedAt().getDayOfMonth());
    }

    @Order(4)
    @DisplayName("성공 - 공유True검색(pageable) ")
    @Test
    void findAllByShareTruePageable() {
        //given
        int pageNo = 0;
        int sizeNo = 2;
        Pageable pageable = PageRequest.of(pageNo, sizeNo, Sort.by("createdAt")
                .descending());
        post1.setCreatedAt(LocalDateTime.of(2022,01,13,21,14));
        post2.setCreatedAt(LocalDateTime.of(2022,01,14,21,14));

        //when
        Page<Post> result = postRepository
                .findAllByShareTrueOrderByCreatedAtDesc(pageable);

        //then
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getShare());
    }

    @Order(5)
    @DisplayName("성공 - 공유True검색")
    @Test
    void findAllByShareTrue() {
        //given
        post1.setCreatedAt(LocalDateTime.of(2022,01,13,21,14));
        post2.setCreatedAt(LocalDateTime.of(2022,01,14,21,14));

        //when
        List<Post> result = postRepository
                .findAllByShareTrueOrderByCreatedAtDesc();

        //then
        assertEquals(1, result.size());
        assertEquals("Post1", result.get(0).getTitle());
    }

    @Order(6)
    @DisplayName("성공 - 게시글ID검색")
    @Test
    void findAllPostId() {
        //when
        Optional<Post> result = postRepository
                .findByIdForHitCount(post2.getId());

        //then
        assertNotEquals(Optional.empty(), result);
        assertEquals("Post2", result.get().getTitle());
    }

    @Order(7)
    @DisplayName("결과없음 - 게시글ID검색")
    @Test
    void findAllPostId2() {
        //when
        Optional<Post> result = postRepository.findByIdForHitCount(99L);

        //then
        assertEquals(Optional.empty(), result);
    }
}