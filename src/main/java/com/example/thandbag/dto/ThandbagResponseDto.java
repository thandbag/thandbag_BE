package com.example.thandbag.dto;

import com.example.thandbag.Enum.Mbti;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
public class ThandbagResponseDto {

    private long userId;
    private String nickname;
    private String lvIcon;
    private String mbti;
    private String title;
    private String content;
    private String createdAt;
    private String imgUrl;
    private boolean closed;
    private String category;
    private boolean share;
    //user의 작성게시글 + 댓글 수
    private int totalCount;
    private int commentCount;
    private List<ShowCommentDto> showCommentDtoList;

}
