package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.Enum.Auth;
import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.model.Alarm;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.AlarmRepository;
import com.example.thandbag.repository.ChatRoomRepository;
import com.example.thandbag.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    AlarmRepository alarmRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;


    @Order(1)
    @DisplayName("알림목록")
    @Test
    void getAlarmList() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository);

        int pageNo = 0;
        int sizeNo = 2;
        Long userId = user.getId();
        Pageable pageable =
                PageRequest.of(pageNo, sizeNo, Sort.by("createdAt")
                        .descending());

        List<Alarm> alarmList = new ArrayList<>();
        alarmList.add(alarm1);
        alarmList.add(alarm2);
        alarmList.add(alarm3);

        Page<Alarm> alarmPage = new PageImpl<>(alarmList);

        given(alarmRepository.findAllByUserIdOrderByIdDesc(userId, pageable))
                .willReturn(alarmPage);

        /* when */
        List<AlarmResponseDto> result =
                alarmService.getAlamList(user, pageNo, sizeNo);

        /* then */
        assertEquals(3, result.size());
        assertEquals(alarm1.getAlarmMessage(), result.get(0).getMessage());
        assertEquals(alarm2.getType().toString(), result.get(1).getType());
        assertEquals(alarm3.getId(), result.get(2).getAlarmId());
    }

    @Order(2)
    @DisplayName("알림 읽었을 경우 체크")
    @Test
    void alarmReadCheck() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(alarmRepository.getById(anyLong())).willReturn(alarm1);

        /* when */
        AlarmResponseDto result =
                alarmService.alarmReadCheck(alarm1.getId(), userDetails);

        /* then */
        assertTrue(result.getIsRead());
        assertEquals(alarm1.getId(), result.getAlarmId());
        assertEquals(alarm1.getAlarmMessage(), result.getMessage());
        assertEquals(alarm1.getType().toString(), result.getType());
    }


    /* 알람 생성을 위한 유저 생성 */
    ProfileImg profileImg = new ProfileImg(
            1L,
            "naver.com"
    );

    User user = new User(
            1L,
            null,
            "hanghae99@hanghae99.kr",
            "test1234!@",
            "생드백",
            "ENTP",
            0,
            1,
            Auth.USER,
            profileImg
    );

    /* 알람 생성 */
    Alarm alarm1 = Alarm.builder()
            .id(1L)
            .userId(user.getId())
            .type(AlarmType.LEVELCHANGE)
            .alarmMessage("레벨이 2로 상승하였습니다.")
            .isRead(false)
            .build();

    Alarm alarm2 = Alarm.builder()
            .id(2L)
            .userId(user.getId())
            .type(AlarmType.LEVELCHANGE)
            .alarmMessage("레벨이 3으로 상승하였습니다.")
            .isRead(false)
            .build();

    Alarm alarm3 = Alarm.builder()
            .id(3L)
            .userId(user.getId())
            .type(AlarmType.LEVELCHANGE)
            .alarmMessage("레벨이 4으로 상승하였습니다.")
            .isRead(false)
            .build();
}