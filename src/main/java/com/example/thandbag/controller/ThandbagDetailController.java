package com.example.thandbag.controller;

import com.example.thandbag.dto.BestUserDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.ThandbagDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ThandbagDetailController {

    private final ThandbagDetailService thandbagDetailService;

    @GetMapping("/api/thandbag/{postId}")
    public ThandbagResponseDto getThandbagDetail(@PathVariable int postId) {
        return thandbagDetailService.getOneThandbag(postId);
    }

    @DeleteMapping("/api/thandbag/{postId}")
    public void removeThandbag (@PathVariable int postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        thandbagDetailService.removeThandbag(postId, userDetails);
    }

    @PostMapping("/api/thandbag")
    public List<BestUserDto> completeThandbag(@RequestParam long postId, @RequestBody List<Long> commentIdList) {
        return thandbagDetailService.completeThandbag(postId, commentIdList);
    }
}
