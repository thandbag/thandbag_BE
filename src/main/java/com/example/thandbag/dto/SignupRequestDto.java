package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String username;
    private String nickname;
    private String password;
    private String mbti;

}
