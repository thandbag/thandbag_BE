package com.example.thandbag.controller;

import com.example.thandbag.dto.mypage.MyPageResponseDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateRequestDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateResponseDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 -> 회원정보 수정
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/mypage/profile")
    public ProfileUpdateResponseDto updateProfile(@RequestBody ProfileUpdateRequestDto updateDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.updateProfile(updateDto, userDetails);
    }

    // 마이페이지 -> 내가 쓴 게시글
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/myThandbag")
    public MyPageResponseDto getMyPostList(@RequestParam int pageNo, @RequestParam int sizeNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPostList(pageNo, sizeNo, userDetails);
    }
}