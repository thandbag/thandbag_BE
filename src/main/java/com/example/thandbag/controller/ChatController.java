package com.example.thandbag.controller;

import com.example.thandbag.model.ChatMessage;
import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.redis.RedisPublisher;
import com.example.thandbag.security.jwt.JwtDecoder;
import com.example.thandbag.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;
    private final ChannelTopic channelTopic;
    private final JwtDecoder jwtDecoder;
    private final RedisPublisher redisPublisher;
    private final RedisTemplate redisTemplate;

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("Authorization") String token) {
        String tokenInfo = token.substring(7);
        System.out.println("Bearer제외한 토큰 : " + tokenInfo);
        System.out.println(message);
        String username = jwtDecoder.decodeUsername(tokenInfo);
        String nickname = chatService.getNickname(username);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(nickname);

        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping
    public ChatRoom createRoom(@RequestParam String name) {
        return chatService.createRoom(name);
    }

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping
    public List<ChatRoom> findAllRoom() {
        return chatService.findAllRoom();
    }

}