package com.example.thandbag.dto.post;

import com.example.thandbag.dto.comment.ShowCommentDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThandbagResponseDto {

    private long userId;
    private long postId;
    private String nickname;
    private int level;
    private String lvImg;
    private String mbti;
    private String title;
    private String content;
    private String createdAt;
    private List<String> imgUrl;
    private boolean closed;
    private String category;
    private boolean share;
    //user의 작성게시글 + 댓글 수
    private int totalCount;
    private int hitCount;
    private int commentCount;
    private List<ShowCommentDto> comments;

}
