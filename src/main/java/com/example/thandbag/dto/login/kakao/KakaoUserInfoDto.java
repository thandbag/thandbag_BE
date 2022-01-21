package com.example.thandbag.dto.login.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoUserInfoDto {

    private Long kakaoId;
    private String nickname;
    private String email;
}
