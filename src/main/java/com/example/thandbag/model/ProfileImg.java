package com.example.thandbag.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
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
