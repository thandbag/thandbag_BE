package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ThandbagRequestDto {

    private String title;
    private String content;
    private List<String> imgUrl;
    private String category;
    private boolean share;
}
