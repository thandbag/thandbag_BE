package com.example.thandbag.service;

import com.example.thandbag.dto.MyPageResponseDto;
import com.example.thandbag.dto.ProfileUpdateRequestDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.ProfileImgRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImgRepository profileImgRepository;

    // 마이페이지 접속 시 필요 정보
    public MyPageResponseDto getMyPage(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Long userId = user.getId();
        String nickname = user.getNickname();
        String profileImgUrl = user.getProfileImg().getProfileImgUrl();
        int level = user.getLevel();
        List<Post> myPostList = postRepository.findAllByUser(user);
        if (myPostList == null) {
            myPostList = new ArrayList<>();
        }

        return new MyPageResponseDto(userId, nickname, profileImgUrl, level, myPostList);
    }

    // 마이페이지에서 회원정보 변경 시, 비밀번호 확인
    public void accessToInfoPage(UserDetailsImpl userDetails, String password) {
        if (!passwordEncoder.matches(userDetails.getPassword(), password)) {
            throw new IllegalArgumentException("비밀번호를 확인해주세요");
        }
    }

    // 회원정보 수정
    @Transactional
    public void updateProfile(ProfileUpdateRequestDto updateDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        // 프로필 이미지가 빈 값이 아니면 수정하기
        if (!"".equals(updateDto.getProfileImgUrl())) {
            Optional<ProfileImg> img = profileImgRepository.findByProfileImgUrl(updateDto.getProfileImgUrl());
            user.setProfileImg(img.get());
        }

        // 닉네임이 빈 값이 아니면 수정하기
        if (!"".equals(updateDto.getNickname())) {
            user.setNickname(updateDto.getNickname());
        }

        // MBTI가 빈 값이 아니면 수정하기
        if (!"".equals(updateDto.getNickname())) {
            user.setNickname(updateDto.getNickname());
        }

        // password가 빈 값이 아니면 수정하기
        if (!"".equals(updateDto.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }
    }
}
