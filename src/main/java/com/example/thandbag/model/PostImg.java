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
public class PostImg {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String postImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public PostImg(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

}
