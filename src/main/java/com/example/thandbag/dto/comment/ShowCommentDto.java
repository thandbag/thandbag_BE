package com.example.thandbag.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowCommentDto {

    private String nickname;
    private int level;
    private String mbti;
    private Long commentId;
    private String comment;
    private String createdAt;
    private long like;
    private boolean currentUserlike;
}
