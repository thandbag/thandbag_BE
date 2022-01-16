package com.example.thandbag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LvImg {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private String lvImgUrl;

    @Column
    private int level;

    public LvImg(String title, String lvImgUrl, int level) {
        this.title = title;
        this.lvImgUrl = lvImgUrl;
        this.level = level;
    }
}
