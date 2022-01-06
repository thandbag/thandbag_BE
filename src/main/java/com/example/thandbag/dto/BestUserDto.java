package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BestUserDto {

    private long userId;
    private String mbti;
    private String nickname;
    private String profileImgUrl;
    private int level;
}
