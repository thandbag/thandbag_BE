package com.example.thandbag.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProfileImg {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String profileImgUrl;

    public ProfileImg(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
