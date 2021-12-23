package com.example.thandbag.service;

import com.example.thandbag.Enum.Mbti;
import com.example.thandbag.dto.MyPageResponseDto;
import com.example.thandbag.dto.MyPostListDto;
import com.example.thandbag.dto.ProfileUpdateRequestDto;
import com.example.thandbag.dto.UpdateProfileResponseDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final PostImgRepository postImgRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImgRepository profileImgRepository;
    private final UserValidator userValidator;

    // 마이페이지 접속 시 필요 정보
    public MyPageResponseDto getMyPage(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Long userId = user.getId();
        String nickname = user.getNickname();
        List<String> profileImgUrl = Arrays.asList("www.naver.com");
        int level = user.getLevel();
        List<Post> myPostList = postRepository.findAllByUser(user);
        if (myPostList == null) {
            myPostList = new ArrayList<>();
        }

        return new MyPageResponseDto(userId, nickname, profileImgUrl, level, myPostList);
    }

    // 마이페이지에서 회원정보 변경 시, 비밀번호 확인
    // 왜 안됨?
    public String accessToInfoPage(String newPassword, UserDetailsImpl userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));

        if (!passwordEncoder.matches(newPassword, user.getPassword())) {
            System.out.println(user.getNickname());
            System.out.println(user.getPassword());
            System.out.println(newPassword);
            throw new IllegalArgumentException("비밀번호를 확인해주세요");
        }

        return "비밀번호 일치";
    }

    // 회원정보 수정
    @Transactional
    public UpdateProfileResponseDto updateProfile(ProfileUpdateRequestDto updateDto, UserDetailsImpl userDetails) {
        Optional<User> user = userRepository.findById(userDetails.getUser().getId());
        Long userId = user.get().getId();
        String profileImgUrl = updateDto.getProfileImgUrl();
        String nickname = updateDto.getNickname();
        Mbti mbti = Mbti.valueOf(updateDto.getMbti());
        String newPassword = updateDto.getNewPassword();

        // 프로필 이미지가 빈 값이 아니면 수정하기
        if (!"".equals(profileImgUrl)) {
            Optional<ProfileImg> img = profileImgRepository.findByProfileImgUrl(profileImgUrl);
            user.get().setProfileImg(img.get());
        }

        // 닉네임 중복 검사
        Optional<User> foundNickname = userRepository.findByNickname(nickname);
        userValidator.checkNickname(foundNickname);

        // 닉네임 유효성 검사
        userValidator.checkNicknameIsValid(nickname);

        // 닉네임이 빈 값이 아니면 수정하기
        if (!"".equals(nickname)) {
            user.get().setNickname(nickname);
        }

        // MBTI가 빈 값이 아니면 수정하기
        if (!"".equals(updateDto.getMbti())) {
            user.get().setMbti(mbti);
        }

        // password 현재와 동일한지 확인하기
        if (passwordEncoder.matches(newPassword, user.get().getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호와 동일합니다.");
        }

        // password 유효성 검사하기
        if (!"".equals(newPassword)) {
            userValidator.checkPassword(newPassword);
            user.get().setPassword(passwordEncoder.encode(newPassword));
        }


        UpdateProfileResponseDto responseDto = new UpdateProfileResponseDto(
                userId,
                profileImgUrl,
                nickname,
                mbti,
                newPassword
        );

        return responseDto;
    }

    // 내가 작성한 생드백 리스트
    public Page<MyPostListDto> getMyPostList(int pageNo, int sizeNo, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        String lvImgUrl = "www.naver.com";

        Pageable pageable = PageRequest.of(pageNo - 1, sizeNo);
        Page<Post> myPostPage = postRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);



        // Page<Post> 를 Page<Dto>로 바꾸는 로직
        Page<MyPostListDto> dtoPage = myPostPage.map(new Function<Post, MyPostListDto>() {
            @Override
            public MyPostListDto apply(Post post) {
                MyPostListDto dto = new MyPostListDto();
                dto.setUserId(user.getId());
                dto.setNickname(user.getNickname());
                dto.setLvIcon(lvImgUrl);
                dto.setTitle(post.getTitle());
                dto.setContent(post.getContent());
                dto.setCreatedAt(post.getCreatedAt());
                dto.setImgUrl(postImgRepository.findByPost(post).get().getPostImgUrl());
                dto.setClosed(post.getClosed());
                dto.setCategory(post.getCategory());
                return dto;
            }
        });
        return dtoPage;
    }
}
