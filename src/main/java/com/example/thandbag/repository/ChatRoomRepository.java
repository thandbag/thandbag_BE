package com.example.thandbag.repository;

import com.example.thandbag.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository  extends JpaRepository<ChatRoom, String> {
//    List<ChatRoom> findAllByPubUserIdOrBySubUserId(Long id);
}
