package com.example.thandbag.model;

import com.example.thandbag.Enum.Category;
import lombok.*;
import com.example.thandbag.dto.post.HitCountDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Boolean closed;

    @Column
    private Boolean share;

    @ManyToOne
    private User user;

    @Column
    private Category category;

    @Column
    private int totalHitCount;

    @OneToMany(cascade = CascadeType.ALL)
    List<PostImg> imgList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    public void closePost() {
        this.closed = true;
    }

    public void updateTotalHit(HitCountDto hitCountDto) {
            this.totalHitCount +=
                    (hitCountDto.getNewHitCount() - hitCountDto.getPrevHitCount());
    }
}
