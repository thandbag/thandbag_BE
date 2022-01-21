package com.example.thandbag.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponseDto {

    private String sender;
    private String senderProfileImg;
    private String message;
    private String createdAt;
}
