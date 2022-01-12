package com.example.thandbag.dto.mypage;

import com.example.thandbag.Enum.Category;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPostListDto {

    private Long postId;
    private Long userId;
    private String nickname;
    private int level;
    private String title;
    private String content;
    private String createdAt;
    private String imgUrl;
    private Boolean closed;
    private Category category;
    private String mbti;
}
