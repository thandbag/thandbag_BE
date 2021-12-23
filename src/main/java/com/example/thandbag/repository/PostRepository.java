package com.example.thandbag.repository;

import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
    Page<Post> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
