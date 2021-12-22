package com.example.thandbag.controller;

import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.service.ThandbagDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ThandbagDetailController {

    private final ThandbagDetailService thandbagDetailService;

    @GetMapping("/api/thandbag/{postId}")
    public ThandbagResponseDto getThandbagDetail(@PathVariable int postId, @RequestParam boolean share) {
        return thandbagDetailService.getOneThandbag(postId, share);
    }

    @DeleteMapping("/api/thandbag/{postId}")
    public void removeThandbag (@PathVariable int postId) {
        thandbagDetailService.removeThandbag(postId);
    }
}
