package com.example.thandbag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Comment extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String comment;

    @Column
    private Boolean likedByWriter;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    public void selectedByPostOwner() {
        this.likedByWriter = true;
    }

}
