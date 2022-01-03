package com.example.thandbag.controller;

import com.example.thandbag.dto.AlarmResponseDto;
import com.example.thandbag.model.User;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AlarmController {

    private final AlarmService alarmService;

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/alarm")
    public List<AlarmResponseDto> getAlarmList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return alarmService.getAlamList(user);
    }
}
