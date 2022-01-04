package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPagePostDto {

    private Long postId;
    private String title;
    private String content;
    private String createdAt;
    private String Category;
}
