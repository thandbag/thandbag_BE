package com.example.thandbag.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@Entity
public class CommentLike extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long user_id;

    private int totalLike;

    @ManyToOne
    private Comment comment;

    public CommentLike() {
        this.totalLike = 0;
    }
}
