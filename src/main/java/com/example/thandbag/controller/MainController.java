package com.example.thandbag.controller;

import com.example.thandbag.dto.ThandbagRequestDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @PostMapping("/api/newThandbag")
    public ThandbagResponseDto createThandbag(@RequestBody ThandbagRequestDto thandbagRequestDto) {
        // @AuthenticationPrincipal UserDetailsImpl userDetails
        return mainService.createThandbag(thandbagRequestDto);
    }

    @GetMapping("/api/thandbagList")
    public List<ThandbagResponseDto> allThandbag(@RequestParam int page, @RequestParam int size) {
        return mainService.showAllThandbag(page, size);
    }

    @GetMapping("/api/thandbag")
    public List<ThandbagResponseDto> searchThandbags(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
        return mainService.searchThandbags(keyword, page, size);
    }
}
