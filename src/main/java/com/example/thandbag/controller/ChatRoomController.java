package com.example.thandbag.controller;

import com.example.thandbag.model.ChatRoom;
import com.example.thandbag.model.LoginInfo;
import com.example.thandbag.repository.ChatRoomRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/user")
    @ResponseBody
    public LoginInfo getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        String name = auth.getName();
        return LoginInfo.builder().name(name).token(JwtTokenUtils.generateJwtToken(user)).build();
    }

    // 채팅 리스트 화면
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    // 모든 채팅방 목록 반환
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chatRoomRepository.findAllRoom();
    }

    // 채팅방 생성
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }

    // 채팅방 입장 화면
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    // 특정 채팅방 조회
    @CrossOrigin(exposedHeaders = "Authorization", originPatterns = "*")
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }
}