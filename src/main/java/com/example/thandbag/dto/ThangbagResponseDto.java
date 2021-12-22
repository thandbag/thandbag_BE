package com.example.thandbag.dto;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.Enum.Mbti;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
public class ThangbagResponseDto {

    private long userId;
    private String nickname;
    private String lvIcon;
    private Mbti mbti;
    private String title;
    private String content;
    private String createdAt;
    private String imgUrl;
    private boolean closed;
    private Category category;
    //user의 작성게시글 + 댓글 수
    private int totalCount;
    private int commentCount;
    private List<ShowCommentDto> showCommentDtoList;

}
