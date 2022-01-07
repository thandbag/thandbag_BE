package com.example.thandbag.dto.alarm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmResponseDto {

    private Long alarmId;
    private String type;
    private String message;
    private Boolean isRead;
    private String chatRoomId;
    private Long postId;
    private Long alarmTargetId;


    public AlarmResponseDto(Long alarmId, String type, String message) {
            this.alarmId = alarmId;
            this.type = type;
            this.message = message;
    }
}