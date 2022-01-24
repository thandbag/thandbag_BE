package com.example.thandbag.model;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.post.HitCountDto;
import lombok.*;
import org.jetbrains.annotations.NotNull;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    private Category category;

    @Column
    private int totalHitCount;

    @Column
    private Integer commentCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<PostImg> imgList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    public void closePost() {
        this.closed = true;
    }

    public void updateTotalHit(HitCountDto hitCountDto) {
            this.totalHitCount += (hitCountDto.getNewHitCount()
                                            - hitCountDto.getPrevHitCount());
    }

    public void plusThandbagCommentCount() {
        commentCount += 1;
    }

    public void minusThandbagCommentCount() {
        commentCount -= 1;
    }

}
