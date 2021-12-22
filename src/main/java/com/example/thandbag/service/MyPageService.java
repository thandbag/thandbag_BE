package com.example.thandbag.service;

import com.example.thandbag.dto.MyPageResponseDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    // 마이페이지 접속 시 필요 정보
    public MyPageResponseDto getMyPage(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        String nickname = userDetails.getUser().getNickname();
        String profileImgUrl = userDetails.getUser().getProfileImg().getProfileImgUrl();
        int level = userDetails.getUser().getLevel();
        List<Post> myPostList = postRepository.findAllByUser(userDetails.getUser());

        return new MyPageResponseDto(userId, nickname, profileImgUrl, level, myPostList);

    }

    // 마이페이지에서 회원정보 변경 시, 비밀번호 확인
    public void accessToInfoPage(UserDetailsImpl userDetails, String password) {
        if (!passwordEncoder.matches(userDetails.getPassword(), password)) {
            throw new IllegalArgumentException("비밀번호를 확인해주세요");
        }
    }



}
