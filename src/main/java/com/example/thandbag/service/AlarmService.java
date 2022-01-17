package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.model.Alarm;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.AlarmRepository;
import com.example.thandbag.repository.ChatRoomRepository;
import com.example.thandbag.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 알림 목록
    public List<AlarmResponseDto> getAlamList(User user, int page, int size) {
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Alarm> alarmListPage = alarmRepository.findAllByUserIdOrderByIdDesc(userId, pageable).getContent();

        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmListPage) {
            // 채팅룸 생성알림일 때
            if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .chatRoomId(chatRoomRepository.findByPubUserIdAndSubUserId(alarm.getPubId(), user.getId()).getId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
                // 게시물에 새로운 댓글이 등록되었을 때
            } else if (alarm.getType().equals(AlarmType.REPLY)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
                // 내 잽이 작성자에게 선택받은 후 종료되었을 때
            } else if (alarm.getType().equals(AlarmType.PICKED)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
                // 레벨업 했을 때
            } else {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            }
        }
        return alarmResponseDtoList;
    }

    // 알림 읽었을 경우 체크
    @Transactional
    public AlarmResponseDto alarmReadCheck(Long alarmId, UserDetailsImpl userDetails) {
        Alarm alarm = alarmRepository.getById(alarmId);
        User user = userDetails.getUser();
        alarm.setIsRead(true);
        alarmRepository.save(alarm);
        AlarmResponseDto alarmDto = new AlarmResponseDto();

        // 새로운 채팅방에 초대 받았을 경우
        if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .chatRoomId(chatRoomRepository.findByPubUserIdAndSubUserId(alarm.getPubId(), user.getId()).getId())
                    .build();
            // 게시물에 새로운 댓글이 등록되었을 때
        } else if (alarm.getType().equals(AlarmType.REPLY)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId())
                    .build();
            // 내 댓글이 작성자에게 선택받았을 때
        } else if (alarm.getType().equals(AlarmType.PICKED)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId())
                    .build();
            // 레벨업 했을 때
        } else {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .build();
        }
        return alarmDto;
    }
}
