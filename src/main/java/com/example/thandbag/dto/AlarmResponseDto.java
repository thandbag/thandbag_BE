package com.example.thandbag.dto;

import lombok.Getter;

@Getter
public class AlarmResponseDto {

    private Long alarmId;
    private String type;
    private String message;
    private String alarmProfileUrl;


    public AlarmResponseDto(Long alarmId, String type, String message) {
            this.alarmId = alarmId;
            this.type = type;
            this.message = message;
            this.alarmProfileUrl = "www.naver.com/bb.jpg";
    }
}