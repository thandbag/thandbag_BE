package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ThandbagRequestDto {

    private String title;
    private String content;
    private String imgUrl;
    private String category;
    private boolean share;
}
