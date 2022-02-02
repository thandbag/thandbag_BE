package com.example.thandbag.repository;

import com.example.thandbag.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "profileImg")
    @Query(value = "select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByKakaoId(Long kakaoId);
}
