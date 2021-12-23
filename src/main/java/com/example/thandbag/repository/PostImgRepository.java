package com.example.thandbag.repository;

import com.example.thandbag.model.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {
    List<PostImg> findAllByPostId(long postId);
}
