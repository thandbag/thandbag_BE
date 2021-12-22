package com.example.thandbag.dto;

import com.example.thandbag.Enum.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ThangbagRequestDto {

    private String title;
    private String content;
    private String imgUrl;
    private Category category;
    private boolean share;
}
