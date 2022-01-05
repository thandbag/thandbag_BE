package com.example.thandbag.service;

import com.example.thandbag.dto.*;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.PostImg;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import com.example.thandbag.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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
        String profileImgUrl = user.getProfileImg().getProfileImgUrl();
        int level = user.getLevel();
        List<MyPagePostDto> myPostDtoList = new ArrayList<>();
        List<Post> myPostList = postRepository.findAllByUser(user);
        //if (myPostList == null) { myPostList = new ArrayList<>(); }
        for (Post post : myPostList) {
            MyPagePostDto myPagePostDto = new MyPagePostDto(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    TimeConversion.timeConversion(post.getCreatedAt()),
                    post.getCategory().getCategory()
            );
            myPostDtoList.add(myPagePostDto);
        }
        return new MyPageResponseDto(userId, nickname, profileImgUrl, level, myPostDtoList);
    }

    // 회원정보 수정
    @Transactional
    public UpdateProfileResponseDto updateProfile(ProfileUpdateRequestDto updateDto, UserDetailsImpl userDetails) {
        User user = userRepository.getById(userDetails.getUser().getId());
        Long userId = user.getId();
        String profileImgUrl = user.getProfileImg().getProfileImgUrl();
        if (updateDto.getProfileImgUrl() != null) {
            profileImgUrl = updateDto.getProfileImgUrl();
        }
        ProfileImg profileImg = profileImgRepository.findByProfileImgUrl(profileImgUrl).get();
        user.setProfileImg(profileImg);

        String nickname = user.getNickname();
        if (updateDto.getNickname() != null) {
            nickname = updateDto.getNickname();
        }
        user.setNickname(nickname);

        String mbti = updateDto.getMbti();
        user.setMbti(mbti);

        // 닉네임 중복 검사
        if(!nickname.equals(user.getNickname())) {
            Optional<User> foundNickname = userRepository.findByNickname(nickname);
            userValidator.checkNickname(foundNickname);
            // 닉네임 유효성 검사
            userValidator.checkNicknameIsValid(nickname);
        }

        return new UpdateProfileResponseDto(
                userId,
                profileImgUrl,
                nickname,
                mbti
        );
    }

    // 내가 작성한 생드백 리스트
    public Page<MyPostListDto> getMyPostList(int pageNo, int sizeNo, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        int level = user.getLevel();

        Pageable pageable = PageRequest.of(pageNo, sizeNo);
        Page<Post> myPostPage = postRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);

        // Page<Post> 를 Page<Dto>로 바꾸는 로직
        return myPostPage.map(post -> {
            Optional<PostImg> postImg = postImgRepository.findByPost(post);
            String postImgUrl = "";
            if(postImg.isPresent())
               postImgUrl = postImg.get().getPostImgUrl();
            MyPostListDto dto = new MyPostListDto();
            dto.setPostId(post.getId());
            dto.setUserId(user.getId());
            dto.setNickname(user.getNickname());
            dto.setLevel(level);
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setCreatedAt(TimeConversion.timeConversion(post.getCreatedAt()));
            dto.setImgUrl(postImgUrl);
            dto.setClosed(post.getClosed());
            dto.setCategory(post.getCategory());
            dto.setMbti(user.getMbti());
            return dto;
        });
    }
}
