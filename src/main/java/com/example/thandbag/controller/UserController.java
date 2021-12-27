package com.example.thandbag.controller;

import com.example.thandbag.dto.LoginRequestDto;
import com.example.thandbag.dto.LoginResultDto;
import com.example.thandbag.dto.SignupRequestDto;
import com.example.thandbag.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
public class UserController {

    private final UserService userService;

    @CrossOrigin("http://localhost:3000")
    @PostMapping("/api/user/signup")
    public String userRegister(@RequestBody SignupRequestDto signupRequestDto){
        userService.userRegister(signupRequestDto);
        return "회원가입 성공";
    }

    @PostMapping("/api/user/login")
    public LoginResultDto userLogin(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return userService.userLogin(loginRequestDto, response);
    }
}