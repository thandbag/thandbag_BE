package com.example.thandbag.service;

import com.example.thandbag.dto.mypage.MyPostListDto;
import com.example.thandbag.dto.mypage.MyPageResponseDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateRequestDto;
import com.example.thandbag.dto.mypage.profile.ProfileUpdateResponseDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.PostImgRepository;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.ProfileImgRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import com.example.thandbag.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final PostImgRepository postImgRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImgRepository profileImgRepository;
    private final UserValidator userValidator;

    // 회원정보 수정
    @Transactional
    public ProfileUpdateResponseDto updateProfile(ProfileUpdateRequestDto updateDto, UserDetailsImpl userDetails) {
        User user = userRepository.getById(userDetails.getUser().getId());
        Long userId = user.getId();
        String profileImgUrl = user.getProfileImg().getProfileImgUrl();
        if (updateDto.getProfileImgUrl() != null) {
            profileImgUrl = updateDto.getProfileImgUrl();
        }
        ProfileImg profileImg = profileImgRepository.findByProfileImgUrl(profileImgUrl).get();
        user.setProfileImg(profileImg);

        // 닉네임 중복검사용
        Optional<User> foundNickname = userRepository.findByNickname(updateDto.getNickname());

        String nickname = user.getNickname();
        if (updateDto.getNickname() != null) {
            // 변경하고자 하는 닉네임과 동일하면 유효성 검사하지 않음
            if (!updateDto.getNickname().equals(user.getNickname())){
            // 닉네임 중복 검사
            userValidator.checkNickname(foundNickname);
            // 닉네임 유효성 검사
            userValidator.checkNicknameIsValid(updateDto.getNickname());
            }
            nickname = updateDto.getNickname();
        }

        user.setNickname(nickname);

        String mbti = updateDto.getMbti();
        user.setMbti(mbti);

        return new ProfileUpdateResponseDto(
                userId,
                profileImgUrl,
                nickname,
                mbti
        );
    }

    // 내가 작성한 생드백 리스트
    public MyPageResponseDto getMyPostList(int pageNo, int sizeNo, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        Pageable sortedByModifiedAtDesc = PageRequest.of(pageNo, sizeNo, Sort.by("modifiedAt").descending());
        List<MyPostListDto> postDtoList = new ArrayList<>();
        List<Post> myPostPage = postRepository.findAllByUserOrderByCreatedAtDesc(user, sortedByModifiedAtDesc).getContent();
        for (Post post: myPostPage) {
            MyPostListDto postDto = MyPostListDto.builder()
                    .postId(post.getId())
                    .userId(post.getUser().getId())
                    .nickname(post.getUser().getNickname())
                    .level(post.getUser().getLevel())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .createdAt(TimeConversion.timeConversion(post.getCreatedAt()))
                    .imgUrl(post.getImgList().size()!=0 ? post.getImgList().get(0).getPostImgUrl() : "")
                    .closed(post.getClosed())
                    .category(post.getCategory())
                    .mbti(post.getUser().getMbti())
                    .build();
            postDtoList.add(postDto);
        }

        MyPageResponseDto responseDto = MyPageResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImg().getProfileImgUrl())
                .level(user.getLevel())
                .mbti(user.getMbti())
                .myPostList(postDtoList)
                .build();

        return responseDto;
    }
}
