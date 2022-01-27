package com.example.thandbag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private Set<CommentLike> commentLikeList;

    public void selectedByPostOwner() {
        this.likedByWriter = true;
    }
}
