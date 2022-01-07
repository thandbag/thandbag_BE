package com.example.thandbag.dto.mypage.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateResponseDto {

    private Long userId;
    private String profileImgUrl;
    private String nickname;
    private String mbti;

}
