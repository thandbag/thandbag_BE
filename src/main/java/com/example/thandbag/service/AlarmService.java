package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.dto.AlarmResponseDto;
import com.example.thandbag.model.Alarm;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 알림 목록
    public List<AlarmResponseDto> getAlamList(User user) {
        List<Alarm> alarmList = alarmRepository.findAllByUserId(user.getId());

        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmList) {
            // 채팅룸 생성알림일때 if 문으로 빌더 생성
            if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(false)
                        .chatRoomId(chatRoomRepository.findByPubUserIdAndSubUserId(alarm.getPubId(), user.getId()).getId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
                // 게시물에 새로운 댓글이 등록되었을 때
            } else if (alarm.getType().equals(AlarmType.REPLY)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(false)
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            } else if (alarm.getType().equals(AlarmType.PICKED)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(false)
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
                // 댓글 선택 받았을 때}
            } else {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(false)
                        .build();
                alarmResponseDtoList.add(alarmDto);
            }
        }
        return alarmResponseDtoList;
    }
}
