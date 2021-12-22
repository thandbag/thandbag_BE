package com.example.thandbag.repository;

import com.example.thandbag.model.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {
    PostImg findByPostId(long postId);
}
