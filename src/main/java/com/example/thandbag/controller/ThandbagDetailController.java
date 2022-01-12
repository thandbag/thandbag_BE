package com.example.thandbag.controller;

import com.example.thandbag.dto.post.BestUserDto;
import com.example.thandbag.dto.post.HitCountDto;
import com.example.thandbag.dto.post.PunchThangbagResponseDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
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
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/thandbag/{postId}")
    public ThandbagResponseDto getThandbagDetail(@PathVariable int postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return thandbagDetailService.getOneThandbag(postId, userDetails.getUser());
    }

    // 생드백 삭제하기
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @DeleteMapping("/api/thandbag/{postId}")
    public void removeThandbag (@PathVariable int postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        thandbagDetailService.removeThandbag(postId, userDetails);
    }

    // 생드백 터뜨리기
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/thandbag")
    public List<BestUserDto> completeThandbag(@RequestParam long postId, @RequestBody HitCountDto hitCountDto) {
        return thandbagDetailService.completeThandbag(postId, hitCountDto);
    }

    // 생드백 때리기
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/thandbag/punch/{postId}")
    public void punchThandBag(@PathVariable Long postId, @RequestBody HitCountDto hitCountDto) {
        thandbagDetailService.updateTotalPunch(postId, hitCountDto);
    }

    // 생드백 때리기 페이지로 이동
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/thandbag/punch/{postId}")
    public PunchThangbagResponseDto getpunchedThandBag(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return thandbagDetailService.getpunchedThandBag(postId, userDetails.getUser());
    }
}
