package com.example.thandbag.service;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.mypage.MyPageResponseDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateRequestDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateResponseDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.ProfileImgRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.validator.UserValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    ProfileImgRepository profileImgRepository;

    @Mock
    ImageService imageService;

    @Mock
    MyPageService myPageService;

    UserValidator userValidator;
    UserDetailsImpl userDetails;

    @BeforeEach
    void setup() {
        this.userValidator = new UserValidator();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("회원정보 수정")
    class updateProfile {

        @Order(1)
        @DisplayName("성공 - 모든항목 수정")
        @Test
        void updateProfileSuccess() throws IOException {
            /* given */
            myPageService = new MyPageService(
                    userRepository,
                    postRepository,
                    profileImgRepository,
                    userValidator,
                    imageService);

            ProfileUpdateRequestDto updateDto = new ProfileUpdateRequestDto(
                    "수정된닉네임",
                    "INFJ"
            );


            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "image.image",
                    "image/png",
                    new FileInputStream(
                            "src/test/resources/templates/testImg/" +
                                    "KakaoTalk_Photo_2021-05-10-00-14-49.jpeg")
            );

            String newProfileImgUrl = "newProfile.jpg";

            userDetails = new UserDetailsImpl(user);

            given(imageService.uploadFile(file)).willReturn(newProfileImgUrl);
            given(userRepository.findByNickname(updateDto.getNickname()))
                    .willReturn(Optional.empty());
            given(userRepository.save(user)).willReturn(user);

            /* when */
            ProfileUpdateResponseDto result =
                    myPageService.updateProfile(file, updateDto, userDetails);

            /* then */
            assertEquals(1L, result.getUserId());
            assertEquals("newProfile.jpg", result.getProfileImgUrl());
            assertEquals("수정된닉네임", result.getNickname());
            assertEquals("INFJ", result.getMbti());
        }

        @Order(2)
        @DisplayName("성공 - 닉네임만 수정")
        @Test
        void updateProfileSuccess2() throws IOException {
            /* given */
            myPageService = new MyPageService(
                    userRepository,
                    postRepository,
                    profileImgRepository,
                    userValidator,
                    imageService);

            ProfileUpdateRequestDto updateDto = new ProfileUpdateRequestDto(
                    "수정된닉네임",
                    "INFJ"
            );

            userDetails = new UserDetailsImpl(user);

            given(userRepository.findByNickname(updateDto.getNickname()))
                    .willReturn(Optional.empty());
            given(userRepository.save(user)).willReturn(user);

            /* when */
            ProfileUpdateResponseDto result =
                    myPageService.updateProfile(null, updateDto, userDetails);

            /*then */
            assertEquals(1L, result.getUserId());
            assertEquals("naver.com", result.getProfileImgUrl());
            assertEquals("수정된닉네임", result.getNickname());
            assertEquals("INFJ", result.getMbti());
        }

        @Order(3)
        @DisplayName("실패 - 닉네임중복")
        @Test
        void updateProfileFail1() {
            /* given */
            myPageService = new MyPageService(
                    userRepository,
                    postRepository,
                    profileImgRepository,
                    userValidator,
                    imageService);

            ProfileUpdateRequestDto updateDto = new ProfileUpdateRequestDto(
                    "샌드백",
                    "INFJ"
            );

            userDetails = new UserDetailsImpl(user);

            User user2 = new User(
                    2L,
                    null,
                    "helloworld@hello.kr",
                    "test1234!@",
                    "샌드백",
                    "ENTP",
                    0,
                    1,
                    Auth.USER,
                    profileImg
            );

            given(userRepository.findByNickname(updateDto.getNickname()))
                    .willReturn(Optional.of(user2));

            /* when */
            Exception exception =
                    assertThrows(IllegalArgumentException.class,
                    () -> myPageService
                            .updateProfile(
                                    null,
                                    updateDto,
                                    userDetails));

            /* then */
            assertEquals("중복된 닉네임이 존재합니다.", exception.getMessage());

        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("생드백 리스트")
    class getMyPostList {

        @Order(1)
        @DisplayName("성공")
        @Test
        void updateProfileSuccess() {
            /* given */
            myPageService = new MyPageService(
                    userRepository,
                    postRepository,
                    profileImgRepository,
                    userValidator,
                    imageService);

            int pageNo = 0;
            int sizeNo = 2;
            userDetails = new UserDetailsImpl(user);

            List<Post> myPostPage = new ArrayList<>();
            post1.setCreatedAt(LocalDateTime.now());
            post2.setCreatedAt(LocalDateTime.now());
            myPostPage.add(post1);
            myPostPage.add(post2);

            Page<Post> posts = new PageImpl<>(myPostPage);

            given(postRepository
                    .findAllByUserOrderByCreatedAtDesc(
                            any(User.class),
                            any(Pageable.class)))
                    .willReturn(posts);


            /* when */
            MyPageResponseDto result =
                    myPageService.getMyPostList(pageNo, sizeNo, userDetails);

            /* then */
            assertEquals(user.getId(), result.getUserId());
            assertEquals(user.getNickname(), result.getNickname());
            assertEquals(user.getProfileImg().getProfileImgUrl(),
                    result.getProfileImgUrl());
            assertEquals(user.getLevel(), result.getLevel());
            assertEquals(user.getMbti(), result.getMbti());
            assertEquals(2, result.getMyPostList().size());
            assertEquals(Category.JOB, result.getMyPostList().get(0).getCategory());
            assertEquals("제목2", result.getMyPostList().get(1).getTitle());
        }
    }

    /* 정보 수정 확인을 위한 유저 생성 */
    ProfileImg profileImg = new ProfileImg(
            1L,
            "naver.com"
    );
    User user = new User(
            1L,
            null,
            "hanghae99@hanghae99.kr",
            "test1234!@",
            "생드백",
            "ENTP",
            0,
            1,
            Auth.USER,
            profileImg
    );

    /* 리스트 테스트를 위한 게시글 생성 */
    Post post1 = new Post(
            1L,
            "제목1",
            "내용1",
            false,
            true,
            user,
            Category.JOB,
            0,
            0,
            new ArrayList<>(),
            new ArrayList<>()
    );

    Post post2 = new Post(
            2L,
            "제목2",
            "내용2",
            true,
            false,
            user,
            Category.SOCIAL,
            5,
            0,
            new ArrayList<>(),
            new ArrayList<>()
    );
}