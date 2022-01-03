package com.example.thandbag.controller;

import com.example.thandbag.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class S3TestController {

    private final PostService postService;

    //이미지 업로드 기능(프론트에서 아직 구현안함)
    @CrossOrigin("*")
    @PostMapping("/api/test/upload")
    public String uploadImg(@RequestBody MultipartFile file) {
        return postService.uploadFile(file);
    }
}
