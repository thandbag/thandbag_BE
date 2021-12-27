package com.example.thandbag.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER,
        TALK,
        JOIN
    }

    private MessageType type;   // 메시지 타입
    private String roomId;      // 방번호
    private String sender;      // 메시지 보낸 사람
    private String message;     // 메시지

}
