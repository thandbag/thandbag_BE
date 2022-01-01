package com.example.thandbag.model;

import javax.persistence.*;

@Entity
public class Alarm extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String notice;

    @Column
    private String type;

    @Column
    private String address;

    // 알림 종류
    // 1, 2, 3, 4, 5
    // [시스템] 생드백 작성자에게 댓글이 선택받았다. -> 클릭 -> 이 게시글로 이동
    // [시스템] 게시글에 댓글이 작성되었다. -> 클릭 -> 이 게시글
    // [시스템] 채팅방이 생성되었다. -> 채팅목록 화면으로 이동 ->
    // [시스템] 레벨이 올랐다. -> 마이페이지로 이동
    // [시스템]
}
