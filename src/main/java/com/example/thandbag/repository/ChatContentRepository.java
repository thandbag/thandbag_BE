package com.example.thandbag.repository;

import com.example.thandbag.model.ChatContent;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatContentRepository extends JpaRepository<ChatContent, Long> {
    Optional<ChatContent> findFirstByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    List<ChatContent> findAllByChatRoomOrderByCreatedAtAsc(ChatRoom room);
    List<ChatContent> findAllByUserNotAndChatRoomAndIsRead(User user, ChatRoom chatRoom, Boolean isRead);
}
