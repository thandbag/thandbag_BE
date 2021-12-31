package com.example.thandbag.repository;

import com.example.thandbag.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository  extends JpaRepository<ChatRoom, String> {
    List<ChatRoom> findAllByPubUserIdOrSubUserId(Long id, Long id2);
}
