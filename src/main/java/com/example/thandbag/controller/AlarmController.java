package com.example.thandbag.controller;

import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.model.User;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AlarmController {

    private final AlarmService alarmService;

    // 알람 리스트 발송
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/api/alarm")
    public List<AlarmResponseDto> getAlarmList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return alarmService.getAlamList(user);
    }

    // 알림 읽음 확인
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/api/alarm/{alarmId}")
    public AlarmResponseDto alarmReadCheck(@PathVariable Long alarmId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return alarmService.alarmReadCheck(alarmId, userDetails);
    }
}
