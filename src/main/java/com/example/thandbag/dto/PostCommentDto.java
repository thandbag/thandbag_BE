package com.example.thandbag.dto;


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
    private boolean currentUserlike;

}
