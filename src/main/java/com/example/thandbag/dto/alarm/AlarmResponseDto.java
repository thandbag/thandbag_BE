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
}