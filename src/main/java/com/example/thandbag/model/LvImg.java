package com.example.thandbag.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor
@Entity
public class LvImg {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String lvImgUrl;

    public LvImg(String lvImgUrl) {
        this.lvImgUrl = lvImgUrl;
    }
}
