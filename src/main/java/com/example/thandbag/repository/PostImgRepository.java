package com.example.thandbag.repository;

import com.example.thandbag.model.Post;
import com.example.thandbag.model.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {
    Optional<PostImg> findByPost(Post post);
}
