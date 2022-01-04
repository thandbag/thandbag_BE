package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDto {

    String profileImgUrl;
    String nickname;
    String mbti;
    String currentPassword;
    String newPassword;
}
