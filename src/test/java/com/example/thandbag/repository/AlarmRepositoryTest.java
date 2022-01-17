package com.example.thandbag.repository;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.Alarm;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class AlarmRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AlarmRepository alarmRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    User user1;
    User user2;
    User user3;
    Post post1;
    Post post2;
    Comment comment1;
    Comment comment2;
    Alarm alarm1;
    Alarm alarm2;
    Alarm alarm3;
    Alarm alarm4;

    @BeforeEach
    void setup() {
        // 유저 생성
        SignupRequestDto signupRequestDto = new SignupRequestDto(
                "test@test.kr",
                "테스트",
                "test1234!@",
                "INFJ"
        );
        this.user1 = new User(signupRequestDto);
        user1.setLevel(2);

        signupRequestDto = new SignupRequestDto(
                "test2@test.kr",
                "테스트2",
                "test1234!@",
                "INFJ"
        );
        this.user2 = new User(signupRequestDto);
        user2.setLevel(3);

        signupRequestDto = new SignupRequestDto(
                "test3@test.kr",
                "테스트3",
                "test1234!@",
                "INFJ"
        );
        this.user3 = new User(signupRequestDto);

        // DB 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Post 생성
        this.post1 = Post.builder()
                .title("Post1")
                .content("Post Content1")
                .closed(false)
                .share(true)
                .user(user1)
                .category(Category.SOCIAL)
                .totalHitCount(0)
                .build();

        this.post2 = Post.builder()
                .title("Post2")
                .content("Post Content2")
                .closed(false)
                .share(true)
                .user(user1)
                .category(Category.LOVE)
                .totalHitCount(0)
                .build();

        // DB 저장
        postRepository.save(post1);
        postRepository.save(post2);

        // 코멘트 생성
        this.comment1 = Comment.builder()
                .comment("코멘트1")
                .user(user2)
                .post(post1)
                .build();

        this.comment2 = Comment.builder()
                .comment("코멘트2")
                .user(user1)
                .post(post1)
                .build();

        // DB 저장
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        // 알림 생성
        // 유저1 레벨업
        alarm1 = Alarm.builder()
                .userId(user1.getId())
                .type(AlarmType.LEVELCHANGE)
                .alarmMessage("레벨이 상승하였습니다.")
                .isRead(false)
                .build();

        // 유저2 레벨업
        alarm2 = Alarm.builder()
                .userId(user2.getId())
                .type(AlarmType.LEVELCHANGE)
                .alarmMessage("레벨이 상승하였습니다.")
                .isRead(false)
                .build();

        // 게시글1에 댓글 알림
        alarm3 = Alarm.builder()
                .userId(post1.getUser().getId())
                .postId(post1.getId())
                .type(AlarmType.REPLY)
                .alarmMessage("게시글에 댓글이 작성되었습니다.")
                .isRead(false)
                .build();

        // 게시글1에 댓글 알림
        alarm4 = Alarm.builder()
                .userId(post1.getUser().getId())
                .postId(post1.getId())
                .type(AlarmType.REPLY)
                .alarmMessage("게시글에 댓글이 작성되었습니다.")
                .isRead(false)
                .build();

        // DB저장
        alarmRepository.save(alarm1);
        alarmRepository.save(alarm2);
        alarmRepository.save(alarm3);
        alarmRepository.save(alarm4);
    }


    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        //when
        List<Alarm> result = alarmRepository.findAll();

        //then
        assertEquals(4, result.size());
        assertEquals(AlarmType.LEVELCHANGE, result.get(0).getType());
        assertEquals(user2.getId(), result.get(1).getUserId());
        assertEquals("게시글에 댓글이 작성되었습니다.", result.get(2).getAlarmMessage());
        assertEquals(AlarmType.REPLY, result.get(3).getType());
    }

    @Order(2)
    @DisplayName("성공 - 유저ID검색")
    @Test
    void findAllByUserIdOrderByIdDesc() {
        //given
        Long userId = user1.getId();
        int pageNo = 0;
        int sizeNo = 10;
        Pageable pageable = PageRequest.of(pageNo, sizeNo, Sort.by("createdAt").descending());

        //when
        List<Alarm> result = alarmRepository.findAllByUserIdOrderByIdDesc(userId, pageable).getContent();

        //then
        assertEquals(3, result.size());
        assertEquals(AlarmType.REPLY, result.get(0).getType());
        assertEquals(userId, result.get(1).getUserId());
        assertEquals("레벨이 상승하였습니다.", result.get(2).getAlarmMessage());
    }

    @Order(3)
    @DisplayName("성공 - 알림삭제(postId)")
    @Test
    void deleteAllByPostId() {
        //given
        Long postId = post1.getId();

        //when
        // 전체 게시글 수를 통한 삭제 확인
        List<Alarm> before = alarmRepository.findAll();
        alarmRepository.deleteAllByPostId(postId);
        List<Alarm> after = alarmRepository.findAll();


        //then
        assertNotEquals(before, after);
        assertEquals(4, before.size());
        assertEquals(2, after.size());
    }

    @Order(4)
    @DisplayName("결과없음 - 유저ID검색")
    @Test
    void findAllByUserIdOrderByIdDesc2() {
        //given
        Long userId = user3.getId();
        int pageNo = 0;
        int sizeNo = 10;
        Pageable pageable = PageRequest.of(pageNo, sizeNo, Sort.by("createdAt").descending());

        //when
        List<Alarm> result = alarmRepository.findAllByUserIdOrderByIdDesc(userId, pageable).getContent();

        //then
        assertEquals(0, result.size());
    }
}