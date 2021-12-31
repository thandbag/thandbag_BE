package com.example.thandbag.dto;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private Long pubId;
    private Long subId;


    public ChatRoomDto (CreateRoomRequestDto createRoomRequestDto) {
        this.roomId = UUID.randomUUID().toString();
        this.pubId = createRoomRequestDto.getPubId();
        this.subId = createRoomRequestDto.getSubId();
    }
}