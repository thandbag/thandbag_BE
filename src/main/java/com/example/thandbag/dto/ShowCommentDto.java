package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowCommentDto {

    private String nickname;
    private String lvIcon;
    private String mbti;
    private String comment;
    private String createdAt;
    private long like;
}