package com.example.thandbag.controller;

import com.example.thandbag.dto.MyPageResponseDto;
import com.example.thandbag.dto.ProfileUpdateRequestDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 메인화면
    @GetMapping("/mypage")
    public MyPageResponseDto getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPage(userDetails);
    }

    // 마이페이지 -> 회원정보 수정 눌렀을 때, 비밀번호 확인
    @PostMapping("/mypage/authentication")
    public void accessToInfoPage(@AuthenticationPrincipal UserDetailsImpl userDetails, String password) {
        myPageService.accessToInfoPage(userDetails, password);
    }

    // 마이페이지 -> 회원정보 수정
    @PostMapping("/mypage/profile")
    public void updateProfile(@RequestBody ProfileUpdateRequestDto updateDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        myPageService.updateProfile(updateDto, userDetails);
    }
}
