package com.example.thandbag.controller;

import com.example.thandbag.dto.mypage.MyPageResponseDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateRequestDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateResponseDto;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;
    private final UserRepository userRepository;

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/mypage/profileTest")
    public ProfileUpdateResponseDto updateProfileTest(@RequestBody ProfileUpdateRequestDto updateDto, @RequestParam String nickname) throws IOException {
        Optional<User> user = userRepository.findByNickname(nickname);
        UserDetailsImpl userDetails = new UserDetailsImpl(user.get());
        return myPageService.updateProfile(null, updateDto, userDetails);
    }

    // 마이페이지 -> 회원정보 수정
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/mypage/profile")
    public ProfileUpdateResponseDto updateProfile(@RequestPart(required = false) MultipartFile file, @RequestPart ProfileUpdateRequestDto updateDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return myPageService.updateProfile(file, updateDto, userDetails);
    }

    // 마이페이지 -> 내가 쓴 게시글
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/myThandbag")
    public MyPageResponseDto getMyPostList(@RequestParam int pageNo, @RequestParam int sizeNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPostList(pageNo, sizeNo, userDetails);
    }
}