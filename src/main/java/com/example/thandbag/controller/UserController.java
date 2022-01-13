package com.example.thandbag.controller;

import com.example.thandbag.dto.login.LoginRequestDto;
import com.example.thandbag.dto.login.LoginResultDto;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.service.KakaoUserService;
import com.example.thandbag.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;

    // 회원가입
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/user/signup")
    public String userRegister(@RequestBody SignupRequestDto signupRequestDto){
        return userService.userRegister(signupRequestDto);
    }

    // 로그인
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/user/login")
    public LoginResultDto userLogin(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return userService.userLogin(loginRequestDto, response);
    }

    // 카카오 로그인
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/user/kakao/callback")
    public LoginResultDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoUserService.kakaoLogin(code, response);
    }
}
