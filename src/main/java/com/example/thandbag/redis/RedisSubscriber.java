package com.example.thandbag.redis;

import com.example.thandbag.dto.ChatMessageDto;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.repository.ChatRoomRepository;
import com.example.thandbag.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            // ChatMessage 객채로 맵핑
            ChatMessageDto chatMessageDto = objectMapper.readValue(publishMessage, ChatMessageDto.class);
            ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDto.getRoomId()).get();
            Long senderId = userRepository.findByNickname(chatMessageDto.getSender()).get().getId();
            Long targetUserId = chatRoom.getPubUserId().equals(senderId) ? chatRoom.getSubUserId() : chatRoom.getPubUserId();

            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);
            messagingTemplate.convertAndSend("/sub/alarm/" + targetUserId, "이거슨 알람용 메시지");
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}