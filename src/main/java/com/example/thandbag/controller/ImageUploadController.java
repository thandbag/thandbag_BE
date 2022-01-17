package com.example.thandbag.controller;

import com.example.thandbag.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ImageUploadController {

    private final ImageService imageService;


}
