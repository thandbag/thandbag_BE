package com.example.thandbag.controller;

import com.example.thandbag.dto.ThangbagRequestDto;
import com.example.thandbag.dto.ThangbagResponseDto;
import com.example.thandbag.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @PostMapping("/api/newThandbag")
    public ThangbagResponseDto createThangbag(@RequestBody ThangbagRequestDto thangbagRequestDto) {

    }

    @GetMapping("/api/thandbagList")
    public List<ThangbagResponseDto> allThangbag(@RequestParam int page, @RequestParam int size) {

    }

    @GetMapping("/api/thandbag")
    public List<ThangbagResponseDto> allThangbag(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {

    }
}
