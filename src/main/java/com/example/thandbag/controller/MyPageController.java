package com.example.thandbag.controller;

import com.example.thandbag.dto.MyPageResponseDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public MyPageResponseDto getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPage(userDetails);
    }

    @PostMapping
    public void accessToInfoPage(@AuthenticationPrincipal UserDetailsImpl userDetails, String password) {
        myPageService.accessToInfoPage(userDetails, password);
    }
}
