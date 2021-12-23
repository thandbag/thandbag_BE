package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class ThandbagRequestDto {

    private String title;
    private String content;
    private List<MultipartFile> imgUrl;
    private String category;
    private boolean share;
}
