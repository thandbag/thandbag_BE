package com.example.thandbag.controller;

import com.example.thandbag.dto.SignupRequestDto;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/api/user/signup")
    public String userRegister(@RequestBody SignupRequestDto signupRequestDto){
        userService.userRegister(signupRequestDto);
        return "회원가입 성공";
    }
}
