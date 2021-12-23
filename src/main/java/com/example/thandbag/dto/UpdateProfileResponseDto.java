package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileResponseDto {

    private Long userId;
    private String profileImgUrl;
    private String nickname;
    private String mbti;
    private String newPassword;

}
