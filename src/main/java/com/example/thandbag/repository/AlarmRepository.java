package com.example.thandbag.repository;

import com.example.thandbag.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    void deleteAllByPostId(Long postId);
}
