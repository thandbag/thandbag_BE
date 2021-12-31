package com.example.thandbag.repository;

import com.example.thandbag.model.ChatContent;
import com.example.thandbag.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatContentRepository extends JpaRepository<ChatContent, Long> {
    Optional<ChatContent> findFirstByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    List<ChatContent> findAllByChatRoomOrderByCreatedAtDesc(ChatRoom room);
}
