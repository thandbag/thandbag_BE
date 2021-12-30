package com.example.thandbag.repository;

import com.example.thandbag.model.ChatContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatContentRepository extends JpaRepository<ChatContent, Long> {
}
