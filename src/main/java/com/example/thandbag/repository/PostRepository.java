package com.example.thandbag.repository;

import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUser(User user);
    Page<Post> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<Post> findAllByShareTrueOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findAllByShareTrueOrderByCreatedAtDesc();
   // @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select p from Post p where p.id = :id")
    Optional<Post> findByIdForHitCount(@Param("id") Long postId);
}
