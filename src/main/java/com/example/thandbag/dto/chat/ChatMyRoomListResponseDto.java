package com.example.thandbag.dto.chat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMyRoomListResponseDto
        implements Comparable<ChatMyRoomListResponseDto> {

    private String roomId;
    private String subNickname;
    private String subProfileImgUrl;
    private String lastContent;
    private String lastContentCreatedTime;
    private int unreadCount;

    /* 알림 창 새 메세지 표시를 위한 마지막으로 메시지 읽은 시간 비교 */
    @Override
    public int compareTo(ChatMyRoomListResponseDto chatMyRoomListResponseDto) {
        return this.lastContentCreatedTime
                .compareTo(chatMyRoomListResponseDto.lastContentCreatedTime);
    }
}
