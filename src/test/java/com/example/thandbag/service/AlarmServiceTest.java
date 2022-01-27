package com.example.thandbag.service;

import com.example.thandbag.Enum.Action;
import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.Enum.Auth;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.AlarmRepository;
import com.example.thandbag.repository.ChatRoomRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    AlarmRepository alarmRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    RedisTemplate redisTemplate;
    @Mock
    ChannelTopic channelTopic;


    @Order(1)
    @DisplayName("알림목록")
    @Test
    void getAlarmList() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);

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
        given(chatRoomRepository
                .findByPubUserIdAndSubUserId(anyLong(), anyLong()))
                .willReturn(chatRoom);

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
    @DisplayName("알림 읽었을 경우 체크 - 레벨")
    @Test
    void alarmReadCheck() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);
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

    @Order(3)
    @DisplayName("알림 읽었을 경우 체크 - 채팅")
    @Test
    void alarmReadCheck2() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(alarmRepository.getById(anyLong())).willReturn(alarm2);
        given(chatRoomRepository
                .findByPubUserIdAndSubUserId(anyLong(), anyLong()))
                .willReturn(chatRoom);

        /* when */
        AlarmResponseDto result =
                alarmService.alarmReadCheck(alarm2.getId(), userDetails);

        /* then */
        assertTrue(result.getIsRead());
        assertEquals(alarm2.getId(), result.getAlarmId());
        assertEquals(alarm2.getAlarmMessage(), result.getMessage());
        assertEquals(alarm2.getType().toString(), result.getType());
    }

    @Order(4)
    @DisplayName("알림 읽었을 경우 체크 - 댓글")
    @Test
    void alarmReadCheck3() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(alarmRepository.getById(anyLong())).willReturn(alarm3);
        /* when */
        AlarmResponseDto result =
                alarmService.alarmReadCheck(alarm3.getId(), userDetails);

        /* then */
        assertTrue(result.getIsRead());
        assertEquals(alarm3.getId(), result.getAlarmId());
        assertEquals(alarm3.getAlarmMessage(), result.getMessage());
        assertEquals(alarm3.getType().toString(), result.getType());
    }

    @Order(5)
    @DisplayName("레벨업 알림 생성")
    @Test
    void generateLevelAlarm_up() {
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);
        /* 레벨1 -> 레벨2 */
        /* given */

        user.setTotalCount(3);
        user.setLevel(1);

        Action action = Action.POST;

        /* when */
        alarmService.generateLevelAlarm(user, action);


        /* 레벨2 -> 레벨3 */
        /* given */
        user.setTotalCount(6);
        user.setLevel(2);

        /* when */
        alarmService.generateLevelAlarm(user, action);

        /* then */
        then(alarmRepository)
                .should(times(2))
                .save(any(Alarm.class));

        then(redisTemplate)
                .should(times(2))
                .convertAndSend(any(),
                        any(AlarmResponseDto.class));




    }

    @Order(6)
    @DisplayName("레벨다운 알림 생성")
    @Test
    void generateLevelAlarm_down() {
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);
        /* 레벨2 -> 레벨1 */
        /* given */

        user.setTotalCount(2);
        user.setLevel(2);

        Action action = Action.DELETE;

        /* when */
        alarmService.generateLevelAlarm(user, action);

        /* 레벨3 -> 레벨2 */
        /* given */

        user.setTotalCount(4);
        user.setLevel(3);

        /* when */
        alarmService.generateLevelAlarm(user, action);

        /* then */
        then(alarmRepository)
                .should(times(2))
                .save(any(Alarm.class));

        then(redisTemplate)
                .should(times(2))
                .convertAndSend(any(),
                        any(AlarmResponseDto.class));
    }

    @Order(7)
    @DisplayName("베스트잽 선정 알림 생성")
    @Test
    void generatePickedAlarm() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);

        post.setCreatedAt(LocalDateTime.now());

        /* when */
        alarmService.generatePickedAlarm(post, comment);

        /* then */
        then(alarmRepository)
                .should(times(1))
                .save(any(Alarm.class));

        then(redisTemplate)
                .should(times(1))
                .convertAndSend(any(),
                        any(AlarmResponseDto.class));
    }

    @Order(8)
    @DisplayName("생드백에 잽이 등록되었을 경우 알림")
    @Test
    void generateNewReplyAlarm() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);

        User postOwner = post.getUser();

        /* when */
        alarmService.generateNewReplyAlarm(postOwner, user2, post);

        /* then */
        then(alarmRepository)
                .should(times(1))
                .save(any(Alarm.class));

        then(redisTemplate)
                .should(times(1))
                .convertAndSend(any(),
                        any(AlarmResponseDto.class));
    }

    @Order(9)
    @DisplayName("본인 생드백에 직접 잽을 등록할 경우 알림X")
    @Test
    void generateNewReplyAlarm_self() {
        /* given */
        AlarmService alarmService =
                new AlarmService(alarmRepository, chatRoomRepository,
                        userRepository, redisTemplate, channelTopic);

        User postOwner = post.getUser();

        /* when */
        alarmService.generateNewReplyAlarm(postOwner, user, post);

        /* then */
        then(alarmRepository)
                .should(never())
                .save(any(Alarm.class));

        then(redisTemplate)
                .should(never())
                .convertAndSend(any(),
                        any(AlarmResponseDto.class));
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

    User user2 = new User(
            2L,
            null,
            "hanghae99@hanghae999.kr",
            "test1234!@",
            "생드백2",
            "ENTP",
            0,
            1,
            Auth.USER,
            profileImg
    );

    Post post = Post.builder()
            .id(1L)
            .title("알림테스트1")
            .category(Category.SOCIAL)
            .closed(false)
            .content("알림테스트내용")
            .share(true)
            .user(user)
            .totalHitCount(0)
            .build();

    Comment comment = Comment.builder()
            .id(1L)
            .comment("알림테스트 댓글")
            .likedByWriter(false)
            .user(user)
            .post(post)
            .commentLikeList(new HashSet<>())
            .build();

    ChatRoom chatRoom = new ChatRoom(
            "chatting-room-test",
            1L,
            2L
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
            .type(AlarmType.INVITEDCHAT)
            .alarmMessage("생드백님과의 채팅이 시작되었습니다.")
            .pubId(2L)
            .isRead(false)
            .build();

    Alarm alarm3 = Alarm.builder()
            .id(3L)
            .userId(user.getId())
            .type(AlarmType.PICKED)
            .alarmMessage("생드백에서 내 댓글이 선정되었습니다.")
            .isRead(false)
            .build();
}