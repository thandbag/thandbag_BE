package com.example.thandbag.repository;

import com.example.thandbag.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserId(Long userId);
    List<Alarm> findAllByUserIdOrderByIdDesc(Long userId);
    Page<Alarm> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    List<Alarm> findAllByPostId(Long postId);
    void deleteAllByPostId(Long postId);
}
