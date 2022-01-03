package com.example.thandbag.model;

import com.example.thandbag.Enum.AlarmType;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Alarm extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // 알람 받는 대상
    @Column
    private Long userId;

    @Column
    private AlarmType type;

    // 채팅방 생성자
    @Column
    private Long pubId;

    // 게시물 번호
    @Column
    private Long postId;

    @Column
    private String alarmMessage;

    public Alarm(Long userId, AlarmType type, String alarmMessage) {
        this.userId = userId;
        this.type = type;
        this.alarmMessage = alarmMessage;
    }


    // type
    //O 1. 채팅방 생성 -> 채팅방 목록으로 이동 -> "새로운 채팅방이 생성되었습니다." (pubId, subId)
    //O 2. 게시글 댓글 -> 게시글 상세로 이동 -> "게시글에 새로운 댓글이 등록되었습니다."  (postId)
    //O 3. 댓글 선택받음 -> 게시글 상세로 이동 -> "내 잽이 생드백 작성자에게 선택받았습니다." (postId)
    //O 4. 레벨이 올랐거나 내렸다 -> 마이페이지로 이동 -> "레벨이 상승했습니다." (--)
    // 5. 댓글 단 샌드백이 터졌을 때, 댓글 작성자에게 -> 게시글 상세로 이동 -> ?

}
