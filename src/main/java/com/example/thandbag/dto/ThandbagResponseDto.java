package com.example.thandbag.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ThandbagResponseDto {

    private long userId;
    private String nickname;
    private String lvIcon;
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
    private int commentCount;
    private List<ShowCommentDto> comments;

}
