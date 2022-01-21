package com.example.thandbag.service;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.ProfileImgRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.validator.UserValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    ProfileImgRepository profileImgRepository;
    @Mock
    HttpServletResponse response;

    UserValidator userValidator;

    /* 중복 확인을 위한 유저 생성 */
    ProfileImg profileImg = new ProfileImg(
            1L,
            "naver.com"
    );
    User existUser = new User(
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

    /* 유저 생성을 위한 항목 */
    String username;
    String nickname;
    String password;
    String mbti;

    @BeforeEach
    void setup() {
        this.userValidator = new UserValidator();
        this.passwordEncoder = new BCryptPasswordEncoder();

        /* 회원가입 시 유효성 체크를 하기 위한, 기본 성공케이스 생성 */
        username = "thandbag@thandbag.kr";
        nickname = "생드백마스터";
        password = "test1234!@";
        mbti = "INFJ";

        /* 존재하는 유저의 비밀번호 암호화 */
        existUser.setPassword(passwordEncoder.encode(existUser.getPassword()));
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("회원가입")
    class userRegister {

        @Order(1)
        @DisplayName("성공")
        @Test
        void userRegisterSuccess() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            String result = userService.userRegister(signupRequestDto);

            /* then */
            assertEquals("회원가입 성공", result);
        }

        @Order(2)
        @DisplayName("실패 - 이메일중복")
        @Test
        void userRegisterFail1() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            username = "hanghae99@hanghae99.kr";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            given(userRepository.findByUsername(signupRequestDto.getUsername()))
                    .willReturn(Optional.of(existUser));

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("중복된 이메일이 존재합니다.",
                    exception.getMessage());
        }

        @Order(3)
        @DisplayName("실패 - 닉네임중복")
        @Test
        void userRegisterFail2() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            nickname = "생드백";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            given(userRepository.findByNickname(signupRequestDto.getNickname()))
                    .willReturn(Optional.of(existUser));

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("중복된 닉네임이 존재합니다.",
                    exception.getMessage());
        }

        @Order(4)
        @DisplayName("실패 - 이메일 빈문자열")
        @Test
        void userRegisterFail3() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            username = "";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("아이디는 필수 입력 값 입니다.",
                    exception.getMessage());
        }

        @Order(5)
        @DisplayName("실패 - 닉네임 빈문자열")
        @Test
        void userRegisterFail4() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            nickname = "";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("닉네임은 필수 입력 값 입니다.",
                    exception.getMessage());
        }

        @Order(6)
        @DisplayName("실패 - 비밀번호 빈문자열")
        @Test
        void userRegisterFail5() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            password = "";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("비밀번호는 필수 입력 값 입니다.",
                    exception.getMessage());
        }

        @Order(7)
        @DisplayName("실패 - 이메일 공백포함")
        @Test
        void userRegisterFail6() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            username = "thand bag@thandbag.kr";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("아이디는 공백을 포함할 수 없습니다.",
                    exception.getMessage());
        }

        @Order(8)
        @DisplayName("실패 - 닉네임 공백포함")
        @Test
        void userRegisterFail7() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            nickname = "생드백 주인";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("닉네임은 공백을 포함할 수 없습니다.",
                    exception.getMessage());
        }

        @Order(9)
        @DisplayName("실패 - 비밀번호 공백포함")
        @Test
        void userRegisterFail8() {
            /* BDD */
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            password = "test 12!@";

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("비밀번호는 공백을 포함할 수 없습니다.",
                    exception.getMessage());
        }

        @Order(10)
        @DisplayName("실패 - 이메일형식")
        @ValueSource(strings = {"thandbag", "thandbag.kr", "thandbag@thandbag"})
        @ParameterizedTest
        void userRegisterFail9(String message) {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            username = message;

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals("이메일 형식을 확인해주세요.",
                    exception.getMessage());
        }

        @Order(11)
        @DisplayName("실패 - 닉네임형식")
        @ValueSource(strings = {"a", "가", "1", "일곱글자테스트", "seventt", "특수문자!"})
        @ParameterizedTest
        void userRegisterFail10(String message) {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            nickname = message;

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals(
                    "닉네임은 영문, 한글, 숫자로 이루어진 2~6자로 작성해주세요.",
                    exception.getMessage());
        }

        @Order(12)
        @DisplayName("실패 - 비밀번호형식")
        @ValueSource(strings =
                {
                        "abcd",
                        "1234",
                        "!@#$",
                        "onlyENGLISH",
                        "12345678",
                        "123456!@",
                        "english!@"
                })
        @ParameterizedTest
        void userRegisterFail11(String message) {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            password = message;

            SignupRequestDto signupRequestDto =
                    new SignupRequestDto(username, nickname, password, mbti);

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userRegister(signupRequestDto));

            /* then */
            assertEquals(
                    "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8자 이상으로 입력해야 합니다.",
                    exception.getMessage());
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("로그인")
    class userLogin {

        @Order(1)
        @DisplayName("성공")
        @Test
        void userLoginSuccess() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            User user = existUser;

            username = "hanghae99@hanghae99.kr";
            password = "test1234!@";

            LoginRequestDto loginRequestDto = new LoginRequestDto(
                    username, password
            );

            given(userRepository.findByUsername(loginRequestDto.getUsername()))
                    .willReturn(Optional.of(user));

            /* when */
            LoginResultDto result =
                    userService.userLogin(loginRequestDto, response);

            /* then */
            assertEquals(user.getId(), result.getUserId());
            assertEquals(user.getNickname(), result.getNickname());
            assertEquals(user.getLevel(), result.getLevel());
            assertEquals(user.getMbti(), result.getMbti());
            assertEquals(user.getProfileImg().getProfileImgUrl(),
                    result.getProfileImgUrl());
        }

        @Order(2)
        @DisplayName("실패-아이디없음")
        @Test
        void userLoginFail1() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            User user = existUser;
            System.out.println(user.getPassword());

            username = "noname@noname.kr";
            password = "test1234!@";

            LoginRequestDto loginRequestDto = new LoginRequestDto(
                    username, password
            );

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userLogin(loginRequestDto, response));

            /* then */
            assertEquals("아이디가 존재하지 않습니다.",
                    exception.getMessage());

        }

        @Order(3)
        @DisplayName("실패-비밀번호틀림")
        @Test
        void userLoginFail2() {
            /* given */
            UserService userService = new UserService(
                    userRepository,
                    passwordEncoder,
                    userValidator,
                    profileImgRepository);

            User user = existUser;
            System.out.println(user.getPassword());

            username = "hanghae99@hanghae99.kr";
            password = "test12!@";

            LoginRequestDto loginRequestDto = new LoginRequestDto(
                    username, password
            );

            given(userRepository.findByUsername(loginRequestDto.getUsername()))
                    .willReturn(Optional.of(user));

            /* when */
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.userLogin(loginRequestDto, response));

            /* then */
            assertEquals("비밀번호를 확인해주세요.", exception.getMessage());
        }
    }
}