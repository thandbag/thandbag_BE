package com.example.thandbag.repository;

import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    List<CommentLike> findAllByUserId(long userId);
    boolean existsByUserId(long userId);
    List<CommentLike> findAllByComment(Comment comment);
}
