package com.example.thandbag.model;

import com.example.thandbag.Enum.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private Long kakaoId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String mbti;

    @Column(nullable = false)
    private int totalCount;

    @Column(nullable = false)
    @ColumnDefault("1")
    private int level;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Long lvImgId;

    @Column(nullable = false)
    private Auth auth;

    @OneToOne
    @JoinColumn(name = "profile_img_id")
    private ProfileImg profileImg;

    public void updateTotalPostsAndComments() {
        this.totalCount += 1;
    }

}
