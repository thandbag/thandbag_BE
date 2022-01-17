package com.example.thandbag.repository;

import com.example.thandbag.model.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {
    List<PostImg> findAllByPostId(long postId);

}
