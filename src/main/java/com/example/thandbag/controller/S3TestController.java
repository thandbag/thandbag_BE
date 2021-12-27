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

    @CrossOrigin("*")
    @PostMapping("/api/test/upload")
    public String uploadImg(@RequestBody MultipartFile file) {
        return postService.uploadFile(file);
    }
}
