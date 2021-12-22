package com.example.thandbag.model;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Mbti;
import com.example.thandbag.dto.SignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Setter
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
    @Enumerated(value = EnumType.STRING)
    private Auth auth;

    @OneToOne
    @JoinColumn(name = "profile_img_id")
    private ProfileImg profileImg;

    public User(SignupRequestDto requestDto){

        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.nickname = requestDto.getNickname();
        this.mbti = Mbti.valueOf(requestDto.getMbti());
        this.totalCount = 0;
        this.level = 1;
        this.lvImgId = 1L;
        this.auth = Auth.USER;
    }
  
    public void updateTotalPostsAndComments() {
        this.totalCount += 1;
    }

}
