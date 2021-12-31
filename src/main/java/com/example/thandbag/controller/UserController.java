package com.example.thandbag.controller;

import com.example.thandbag.dto.LoginRequestDto;
import com.example.thandbag.dto.LoginResultDto;
import com.example.thandbag.dto.SignupRequestDto;
import com.example.thandbag.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/user/signup")
    public String userRegister(@RequestBody SignupRequestDto signupRequestDto){
        userService.userRegister(signupRequestDto);
        return "회원가입 성공";
    }

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/user/login")
    public LoginResultDto userLogin(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return userService.userLogin(loginRequestDto, response);
    }
}
