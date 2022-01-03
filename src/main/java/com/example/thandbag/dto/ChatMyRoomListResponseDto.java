package com.example.thandbag.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMyRoomListResponseDto implements Comparable<ChatMyRoomListResponseDto> {

    private String roomId;
    private String subNickname;
    private String subProfileImgUrl;
    private String lastContent;
    private String lastContentCreatedTime;
    private int unreadCount;

    @Override
    public int compareTo(ChatMyRoomListResponseDto chatMyRoomListResponseDto) {
        return this.lastContentCreatedTime.compareTo(chatMyRoomListResponseDto.lastContentCreatedTime);
    }
}
