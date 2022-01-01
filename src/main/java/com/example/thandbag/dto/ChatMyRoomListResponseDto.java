package com.example.thandbag.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMyRoomListResponseDto {

    private String roomId;
    private String subNickname;
    private String subProfileImgUrl;
    private String lastContent;
    private String lastContentCreatedTime;
    private int unreadCount;

    // 새로운 메시지가 몇개인지는 나중에 추가하기로 함
}
