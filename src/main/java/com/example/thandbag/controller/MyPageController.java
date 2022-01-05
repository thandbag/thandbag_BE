package com.example.thandbag.controller;

import com.example.thandbag.dto.MyPageResponseDto;
import com.example.thandbag.dto.MyPostListDto;
import com.example.thandbag.dto.ProfileUpdateRequestDto;
import com.example.thandbag.dto.UpdateProfileResponseDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 메인화면
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/mypage")
    public MyPageResponseDto getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPage(userDetails);
    }

//    // 마이페이지 -> 회원정보 수정 눌렀을 때, 비밀번호 확인
//    @PostMapping("/mypage/authentication")
//    public String accessToInfoPage(@RequestBody String newPassword, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return myPageService.accessToInfoPage(newPassword, userDetails);
//
//    }

    // 마이페이지 -> 회원정보 수정
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/mypage/profile")
    public UpdateProfileResponseDto updateProfile(@RequestBody ProfileUpdateRequestDto updateDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.updateProfile(updateDto, userDetails);
    }

    // 마이페이지 -> 내가 쓴 게시글
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/myThandbag")
    public Page<MyPostListDto> getMyPostList(@RequestParam int pageNo, @RequestParam int sizeNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPostList(pageNo, sizeNo, userDetails);
    }
}