package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.dto.AlarmResponseDto;
import com.example.thandbag.dto.PostCommentDto;
import com.example.thandbag.dto.ShowCommentDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final AlarmRepository alarmRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    // 잽 작성
    @Transactional
    public PostCommentDto postComment(long postId, String content, UserDetailsImpl userDetails) {
        Comment comment = Comment.builder()
                .comment(content)
                .likedByWriter(false)
                .user(userDetails.getUser())
                .post(postRepository.getById(postId))
                .build();
        postRepository.getById(postId).getCommentList().add(comment);
        comment = commentRepository.save(comment);

        // 생드백+잽 수 count
        User user = userDetails.getUser();
        user.plusTotalPostsAndComments();
        userRepository.save(user);

        // 레벨업
        int totalPosts = user.getTotalCount();
        if (totalPosts < 5 && totalPosts > 2 && user.getLevel() == 1) {
            user.setLevel(2);

            // 레벨업 알림 생성
            Alarm levelAlarm2 = new Alarm(
                    user.getId(),
                    AlarmType.LEVELCHANGE,
                    "레벨이 상승하였습니다."
            );
            alarmRepository.save(levelAlarm2);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelAlarm2.getId())
                    .type(levelAlarm2.getType().toString())
                    .message("[알림] 레벨이 상승하였습니다.")
                    .alarmTargetId(user.getId())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        } else if (totalPosts >= 5) {
            user.setLevel(3);
            // 레벨업 알림 생성
            Alarm levelAlarm3 = new Alarm(
                    user.getId(),
                    AlarmType.LEVELCHANGE,
                    "레벨이 상승하였습니다."
            );
            alarmRepository.save(levelAlarm3);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelAlarm3.getId())
                    .type(levelAlarm3.getType().toString())
                    .message("[알림] 레벨이 상승하였습니다.")
                    .alarmTargetId(user.getId())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        }

        User postOwner = userRepository.getById(postId);

        // 알림 생성
        Alarm alarm = Alarm.builder()
                .userId(postOwner.getId())
                .type(AlarmType.LEVELCHANGE)
                .alarmMessage("[" + postRepository.getById(postId).getTitle() + "] 게시글에 잽이 등록되었습니다.")
                .build();

        alarmRepository.save(alarm);

        // 알림 메시지를 보낼 DTO 생성
        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .alarmId(alarm.getId())
                .type(alarm.getType().toString())
                .message("[알림] 게시글에 잽 등록 알림")
                .alarmTargetId(postOwner.getId())
                .build();

        // redis로 알림메시지 pub
        redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);

        return new PostCommentDto(
                userDetails.getUser().getId(),
                userDetails.getUser().getNickname(),
                comment.getId(),
                content,
                TimeConversion.timeConversion(comment.getCreatedAt()),
                userDetails.getUser().getTotalCount(),
                0L,
                false
        );
    }

    // 잽 삭제
    public void deleteComment(long commentId, UserDetailsImpl userDetails) {
        commentRepository.deleteById(commentId);
        User user = userRepository.getById(userDetails.getUser().getId());
        user.minusTotalPostsAndComments();

        //leveldown
        int totalPosts = user.getTotalCount();
        if (totalPosts <= 2 && user.getLevel() == 2) {
            user.setLevel(1);
            // 레벨다운 알림 생성
            Alarm levelDown1 = new Alarm(
                    user.getId(),
                    AlarmType.LEVELCHANGE,
                    "레벨이 하락하였습니다."
            );
            alarmRepository.save(levelDown1);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelDown1.getId())
                    .type(levelDown1.getType().toString())
                    .message("[알림] 레벨이 하락하였습니다.")
                    .alarmTargetId(user.getId())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        } else if (totalPosts < 5 && user.getLevel() == 3) {
            user.setLevel(2);
            // 레벨다운 알림 생성
            Alarm levelDown2 = new Alarm(
                    user.getId(),
                    AlarmType.LEVELCHANGE,
                    "레벨이 하락하였습니다."
            );
            alarmRepository.save(levelDown2);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelDown2.getId())
                    .type(levelDown2.getType().toString())
                    .message("[알림] 레벨이 하락하였습니다.")
                    .alarmTargetId(user.getId())
                    .build();

            //레벨 다운 알림 redis로 pub
            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        }
    }

    // 잽 좋아요
    @Transactional
    public ShowCommentDto likeComment(long commentId, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.getById(commentId);
        Post post = comment.getPost();
        // 댓글 좋아요 하는 사람이 게시글 작성자라면 댓글에 작성자에게 선택되었다고 체크
        if (post.getUser().getId().equals(userDetails.getUser().getId())) {
            comment.selectedByPostOwner();
            commentRepository.save(comment);
        }
        // 이미 좋아요 했으면 좋아요 취소
        CommentLike commentLike = commentLikeRepository.findByUserIdAndComment(userDetails.getUser().getId(), comment);
        if (commentLike != null) {
            commentLikeRepository.delete(commentLike);
        } else {
            // 안했으면 좋아요 + 1
            commentLikeRepository.save(
                    CommentLike.builder()
                            .userId(userDetails.getUser().getId())
                            .comment(commentRepository.getById(commentId))
                            .build());
        }

        return new ShowCommentDto(
                comment.getUser().getNickname(),
                comment.getUser().getLevel(),
                comment.getUser().getMbti(),
                comment.getId(),
                comment.getComment(),
                TimeConversion.timeConversion(comment.getCreatedAt()),
                commentLikeRepository.findAllByComment(comment).size(),
                commentLikeRepository.existsByUserId(userDetails.getUser().getId())
        );
    }
}
