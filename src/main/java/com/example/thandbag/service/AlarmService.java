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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    /* 레벨 알림 관련 */
    /* 0개~2개 = 레벨1 */
    static final int LV1_MAX = 2;

    /* 3개~4개 = 레벨2 */
    static final int LV2_MAX = 4;
    static final int LV2_MIN = 3;

    /* 5개 이상 = 레벨3 */
    static final int LV3_MIN = 5;

    /* 레벨 */
    static final int LV1 = 1;
    static final int LV2 = 2;
    static final int LV3 = 3;

    /* 게시물업로드인지, 삭제인지 확인 */
    public enum Action {
        POST,
        DELETE
    }

    /* 레벨 관련 알림 보내기 */
    public void generateLevelAlarm(User user, Action action) {

        int currentLv = user.getLevel();
        int postCount = user.getTotalCount();

        /* 레벨1에서 레벨2로 상승할 때 */
        if ((postCount <= LV2_MAX && postCount >= LV2_MIN && currentLv == LV1)
                || (postCount >= LV3_MIN && currentLv == LV2)
                || (postCount <= LV1_MAX && currentLv == LV2)
                || (postCount <= LV2_MAX && currentLv == LV3)) {


            String alarmMessage = action == Action.POST
                    ? "[알림] 레벨이 " + (currentLv + 1) + "로 상승하였습니다."
                    : "[알림] 레벨이 " + (currentLv - 1) + "로 하락하였습니다.";

            user.setLevel(action == Action.POST
                    ? (currentLv + 1)
                    : (currentLv - 1));

            /* 레벨업 알림 생성 */
            Alarm levelAlarm = Alarm.builder()
                    .userId(user.getId())
                    .type(AlarmType.LEVELCHANGE)
                    .alarmMessage(alarmMessage)
                    .isRead(false)
                    .build();

            alarmRepository.save(levelAlarm);

            /* 알림 메시지를 보낼 DTO 생성 */
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelAlarm.getId())
                    .type(levelAlarm.getType().toString())
                    .message(alarmMessage)
                    .alarmTargetId(user.getId())
                    .isRead(levelAlarm.getIsRead())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(),
                    alarmResponseDto);
        }
    }


    /* 알림 목록 */
    public List<AlarmResponseDto> getAlamList(User user, int page, int size) {
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt")
                .descending());

        List<Alarm> alarmListPage = alarmRepository
                .findAllByUserIdOrderByIdDesc(userId, pageable).getContent();

        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmListPage) {
            /* 채팅룸 생성알림일 때 */
            if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .chatRoomId(chatRoomRepository
                                .findByPubUserIdAndSubUserId(
                                        alarm.getPubId(),
                                        user.getId()).getId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            /* 게시물에 새로운 댓글이 등록되었을 때 */
            } else if (alarm.getType().equals(AlarmType.REPLY)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            /* 내 잽이 작성자에게 선택받은 후 종료되었을 때 */
            } else if (alarm.getType().equals(AlarmType.PICKED)) {
                AlarmResponseDto alarmDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .message(alarm.getAlarmMessage())
                        .isRead(alarm.getIsRead())
                        .postId(alarm.getPostId())
                        .build();
                alarmResponseDtoList.add(alarmDto);
            /* 레벨업 했을 때 */
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

    /* 알림 읽었을 경우 체크 */
    @Transactional
    public AlarmResponseDto alarmReadCheck(Long alarmId,
                                           UserDetailsImpl userDetails) {
        Alarm alarm = alarmRepository.getById(alarmId);
        User user = userDetails.getUser();
        alarm.setIsRead(true);
        alarmRepository.save(alarm);
        AlarmResponseDto alarmDto = new AlarmResponseDto();

        /* 새로운 채팅방에 초대 받았을 경우 */
        if (alarm.getType().equals(AlarmType.INVITEDCHAT)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .chatRoomId(chatRoomRepository
                            .findByPubUserIdAndSubUserId(
                                    alarm.getPubId(),
                                    user.getId()).getId())
                    .build();
        /* 게시물에 새로운 댓글이 등록되었을 때 */
        } else if (alarm.getType().equals(AlarmType.REPLY)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId())
                    .build();
        /* 내 댓글이 작성자에게 선택받았을 때 */
        } else if (alarm.getType().equals(AlarmType.PICKED)) {
            alarmDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .type(alarm.getType().toString())
                    .message(alarm.getAlarmMessage())
                    .isRead(alarm.getIsRead())
                    .postId(alarm.getPostId())
                    .build();
        /* 레벨업 했을 때 */
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
