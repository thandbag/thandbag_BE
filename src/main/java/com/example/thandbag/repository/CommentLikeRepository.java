package com.example.thandbag.repository;

import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByUserIdAndComment(long userId, Comment comment);
    List<CommentLike> findAllByComment(Comment comment);
    boolean existsByCommentAndUserId(Comment comment, Long userId);
}
