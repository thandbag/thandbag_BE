package com.example.thandbag.dto.post;

import com.example.thandbag.dto.comment.ShowCommentDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThandbagResponseDto {

    private long userId;
    private Long postId;
    private String nickname;
    private int level;
    private String lvImg;
    private String mbti;
    private String title;
    private String content;
    private String createdAt;
    private String profileImgUrl;
    private List<String> imgUrl;
    private boolean closed;
    private String category;
    private boolean share;
    private int totalCount; // user의 작성게시글 + 댓글 수
    private int hitCount;
    private Integer commentCount;
    private List<ShowCommentDto> comments;
}
