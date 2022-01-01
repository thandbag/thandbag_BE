package com.example.thandbag.controller;

import com.example.thandbag.dto.BestUserDto;
import com.example.thandbag.dto.PunchThangbagResponseDto;
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

    // 생드백 상세보기
    @CrossOrigin("*")
    @GetMapping("/api/thandbag/{postId}")
    public ThandbagResponseDto getThandbagDetail(@PathVariable int postId) {
        return thandbagDetailService.getOneThandbag(postId);
    }

    // 생드백 삭제하기
    @CrossOrigin("*")
    @DeleteMapping("/api/thandbag/{postId}")
    public void removeThandbag (@PathVariable int postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        thandbagDetailService.removeThandbag(postId, userDetails);
    }

    // 생드백 터뜨리기
    @CrossOrigin("*")
    @PostMapping("/api/thandbag")
    public List<BestUserDto> completeThandbag(@RequestParam long postId) {
        return thandbagDetailService.completeThandbag(postId);
    }

    @CrossOrigin("*")
    @PostMapping("/api/thandbag/punch/{postId}")
    public void punchThandBag(@PathVariable Long postId, @RequestBody int totalHitCount) {
        thandbagDetailService.updateTotalPunch(postId, totalHitCount);
    }

    @CrossOrigin("*")
    @GetMapping("/api/thandbag/punch/{postId}")
    public PunchThangbagResponseDto getpunchedThandBag(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return thandbagDetailService.getpunchedThandBag(postId, userDetails.getUser());
    }
}
