package com.example.thandbag.repository;

import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByComment(Comment comment);
}
