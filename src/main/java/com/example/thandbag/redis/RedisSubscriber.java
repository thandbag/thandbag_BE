package com.example.thandbag.redis;

import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.dto.chat.ChatMessageDto;
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

    /**
     * Redis에서 메시지가 발행(publish)되면
     * 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            /* 채팅 메세지 보내기의 pub 이라면
               채팅 소켓으로 채팅방을 구독한 클라이언트에게 메시지 발송 */
            if (!publishMessage.contains("[알림]")) {

                ChatMessageDto chatMessageDto = objectMapper
                        .readValue(publishMessage, ChatMessageDto.class);

                chatMessageDto.setSenderProfileImg(
                        userRepository.findByNickname(chatMessageDto.getSender())
                                .get()
                                .getProfileImg()
                                .getProfileImgUrl());

                messagingTemplate.convertAndSend(
                        "/sub/chat/room/" + chatMessageDto.getRoomId(),
                                    chatMessageDto);

            /* 알림 메세지 보내기의 pub 이라면 알림 소켓으로 알림 수신자에게 메시지 발송 */
            } else {
                AlarmResponseDto alarmResponseDto = objectMapper
                        .readValue(publishMessage, AlarmResponseDto.class);

                messagingTemplate.convertAndSend(
                        "/sub/alarm/" + alarmResponseDto.getAlarmTargetId(),
                                    alarmResponseDto);
            }
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}

