package com.example.thandbag.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AlarmGenerator {
//
//    private final AlarmRepository alarmRepository;
//    private final RedisTemplate redisTemplate;
//    private final ChannelTopic channelTopic;
//
//    /* 0개~2개 = 레벨1 */
//    static final int LV1_MAX = 2;
//
//    /* 3개~4개 = 레벨2 */
//    static final int LV2_MAX = 4;
//    static final int LV2_MIN = 3;
//
//    /* 5개 이상 = 레벨3 */
//    static final int LV3_MIN = 5;
//
//    static final int LV1 = 1;
//    static final int LV2 = 2;
//    static final int LV3 = 3;
//
//    public enum Action {
//        POST,
//        DELETE
//    }
//
//    /* 레벨 관련 알림 */
//    static void generateLevelAlarm(User user, Action action) {
//
//        int currentLv = user.getLevel();
//        int postCount = user.getTotalCount();
//
//        /* 레벨1에서 레벨2로 상승할 때 */
//        if ((postCount <= LV2_MAX && postCount >= LV2_MIN && currentLv == LV1)
//            || (postCount >= LV3_MIN && currentLv == LV2)
//            || (postCount <= LV1_MAX && currentLv == LV2)
//            || (postCount <= LV2_MAX && currentLv == LV3)) {
//
//
//            String alarmMessage = action == Action.POST
//                    ? "[알림] 레벨이 " + (currentLv + 1) + "로 상승하였습니다."
//                    : "[알림] 레벨이 " + (currentLv - 1) + "로 하락하였습니다.";
//
//            user.setLevel(action == Action.POST
//                    ? (currentLv + 1)
//                    : (currentLv - 1));
//
//            /* 레벨업 알림 생성 */
//            Alarm levelAlarm = Alarm.builder()
//                    .userId(user.getId())
//                    .type(AlarmType.LEVELCHANGE)
//                    .alarmMessage(alarmMessage)
//                    .isRead(false)
//                    .build();
//
//            alarmRepository.save(levelAlarm);
//
//            /* 알림 메시지를 보낼 DTO 생성 */
//            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
//                    .alarmId(levelAlarm.getId())
//                    .type(levelAlarm.getType().toString())
//                    .message(alarmMessage)
//                    .alarmTargetId(user.getId())
//                    .isRead(levelAlarm.getIsRead())
//                    .build();
//
//            redisTemplate.convertAndSend(channelTopic.getTopic(),
//                    alarmResponseDto);
//        } else {
//            throw new IllegalArgumentException("레벨이 변경되지 않았습니다.");
//        }
//    }
}
