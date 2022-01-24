package com.example.thandbag.repository;

import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUser(User user);
    Page<Post> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<Post> findAllByShareTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "select p from Post p JOIN FETCH " +
                    "p.user u JOIN FETCH u.profileImg where p.share = true")
    List<Post> findAllByShareTrueOrderByCreatedAtDesc();

    @Query(value = "select p from Post p where p.id = :id")
    Optional<Post> findByIdForHitCount(@Param("id") Long postId);

    /* 닉네임, 게시글 제목, 게시글 내용 안에 키워드가 포함되는 글들을 리턴 (검색 쿼리) */
    @Query(value = "select p from Post p where p.share = true and " +
            "(p.title like %:keyword% or p.content like %:keyword% or " +
            "p.user in (select u from User u where u.nickname like %:keyword%))")
    Page<Post> findAllByShareTrueAndContainsKeywordForSearch(
            @Param("keyword") String keyword, Pageable pageable);

}
