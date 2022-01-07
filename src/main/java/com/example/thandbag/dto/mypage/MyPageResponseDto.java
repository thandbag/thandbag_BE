package com.example.thandbag.dto.mypage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private int level;
    private List<MyPagePostDto> myPostList;

}
