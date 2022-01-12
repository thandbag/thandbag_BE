package com.example.thandbag.dto.mypage;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private int level;
    private String mbti;
    private List<MyPostListDto> myPostList;

}
