package com.example.thandbag.dto.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostCommentDto {

    private long userId;
    private String nickname;
    private Long commentId;
    private String comment;
    private String createdAt;
    private long totalCount;
    private Long like;
    private String mbti;
    private boolean currentUserlike;
    private String profileImgUrl;
    private int level;

}
