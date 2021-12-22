package com.example.thandbag.controller;

import com.example.thandbag.dto.ThangbagResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ThandbagDetailController {

    @GetMapping("/api/thandbag/{postId}")
    public ThangbagResponseDto getThangbagDetail(@PathVariable int postId, @RequestParam boolean share) {

    }

    @DeleteMapping("/api/thandbag/{postId}")
    public void removeThandbag (@PathVariable int postId) {

    }
}
