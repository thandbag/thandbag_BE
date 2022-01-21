package com.example.thandbag.service;

import com.example.thandbag.dto.login.kakao.KakaoUserInfoDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.ProfileImgRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.security.jwt.JwtTokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImgRepository profileImgRepository;

    /* 카카오 로그인 */
    public LoginResultDto kakaoLogin(String code, HttpServletResponse response)
            throws JsonProcessingException {
        /* 인가 코드로 액세스 토큰 요청 */
        String accessToken = getAccessToken(code);

        /* 토큰으로 카카오 API 호출 */
        KakaoUserInfoDto kakaoUserInfoDto = getKakaoInfo(accessToken);

        /* DB에서 kakao Id User 확인, 필요 시 회원가입 */
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfoDto);

        /* 강제 로그인 처리 */
        forceLogin(kakaoUser, response);

        LoginResultDto loginResultDto= new LoginResultDto(
                kakaoUser.getId(),
                kakaoUser.getNickname(),
                kakaoUser.getLevel(),
                kakaoUser.getMbti(),
                kakaoUser.getProfileImg().getProfileImgUrl()
        );

        return loginResultDto;
    }

    /* 액세스 토큰 요청 함수 */
    private String getAccessToken(String code) throws JsonProcessingException {
        /* HTTP Header 생성 */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type",
                "application/x-www-form-urlencoded;charset=utf-8");

        /* HTTP Body 생성 */
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "2bbe979f5fff3c4ab9f79ad6a7be6729");
        body.add("redirect_uri", "https://thandbag.com/user/kakao/callback");
        body.add("code", code);

        /* HTTP 요청 보내기 */
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        /* response는 json 형식 */
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        /* HTTP 응답 (JSON) -> 액세스 토큰 파싱 */
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    /* 필요 시 회원가입 함수 */
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfoDto) {
        /* DB 에 중복된 Kakao Id 가 있는지 확인 */
        Long kakaoId = kakaoUserInfoDto.getKakaoId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            /* 회원가입 */
            /* username: kakao nickname */
            String nickname = kakaoUserInfoDto.getNickname();

            /* password: random UUID */
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            /* email: kakao email */
            String email = kakaoUserInfoDto.getEmail();

            User user = new User(
                    email,
                    nickname,
                    encodedPassword,
                    "맞춰보셈",
                    kakaoId
            );

            /* 최초 회원가입 시 기본 프로필 이미지 랜덤으로 부여 */
            Random random = new Random();
            Long randomNum = (long) random.nextInt(3) + 1;
            ProfileImg profileImg = profileImgRepository.getById(randomNum);
            user.setProfileImg(profileImg);
            user.setLevel(1);

            kakaoUser = userRepository.save(user);
        }
        return kakaoUser;
    }


    /* 토큰으로 카카오 API 호출 함수 */
    private KakaoUserInfoDto getKakaoInfo(String accessToken)
            throws JsonProcessingException {
        /* HTTP Header 생성 */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",
                "Bearer " + accessToken);
        headers.add("Content-type",
                "application/x-www-form-urlencoded;charset=utf-8");

        /* HTTP 요청 보내기 */
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest =
                new HttpEntity<>(headers);

        RestTemplate rt = new RestTemplate();

        /* response는 json 형식 */
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email;

        if (jsonNode.get("kakao_account").get("email") == null) {
            email = getSaltString() + "@kakaoUser.kr";
        } else {
            email = jsonNode.get("kakao_account").get("email").asText();
        }

        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto(
                id,
                nickname,
                email
        );

        return kakaoUserInfoDto;
    }

    /* 강제 로그인 함수 */
    private void forceLogin(User kakaoUser, HttpServletResponse response) {
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtTokenUtils.generateJwtToken(userDetails);

        response.addHeader("Authorization", "Bearer " + token);
    }

    /* 카카오 로그인 시, 회원용 이메일 랜덤으로 생성하도록 만들기 */
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { /*랜덤 이메일 길이 = 10자 미만*/
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        return salt.toString();
    }

}
