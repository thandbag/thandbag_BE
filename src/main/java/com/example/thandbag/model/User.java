package com.example.thandbag.model;

import com.example.thandbag.Enum.Auth;
import com.example.thandbag.dto.signup.SignupRequestDto;
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
    @Enumerated(value = EnumType.STRING)
    private Auth auth;

    @ManyToOne
    private ProfileImg profileImg;

    public User(SignupRequestDto requestDto){
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.nickname = requestDto.getNickname();
        this.mbti = requestDto.getMbti();
        this.totalCount = 0;
        this.level = 1;
        this.auth = Auth.USER;
    }

    public User(String username, String nickname, String password, String mbti, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.mbti = mbti;
        this.totalCount = 0;
        this.level = 1;
        this.auth = Auth.USER;
        this.kakaoId = kakaoId;
    }

    public void plusTotalPostsAndComments() {
        this.totalCount += 1;
    }
    public void minusTotalPostsAndComments()   {
        this.totalCount -= 1;
    }

}
