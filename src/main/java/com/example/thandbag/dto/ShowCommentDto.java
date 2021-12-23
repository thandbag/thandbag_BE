package com.example.thandbag.dto;

import com.example.thandbag.Enum.Mbti;
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
