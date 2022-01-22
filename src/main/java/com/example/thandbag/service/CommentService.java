package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.dto.comment.PostCommentDto;
import com.example.thandbag.dto.comment.ShowCommentDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.AlarmService.Action;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AlarmService alarmService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final AlarmRepository alarmRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    /* 잽 작성 */
    @Transactional
    public PostCommentDto postComment(long postId, String content,
                                      UserDetailsImpl userDetails) {
        Comment comment = Comment.builder()
                .comment(content)
                .likedByWriter(false)
                .user(userDetails.getUser())
                .post(postRepository.getById(postId))
                .build();
        comment = commentRepository.save(comment);

        /* 생드백 + 잽 수 count */
        User user = userDetails.getUser();
        user.plusTotalPostsAndComments();

        /* 레벨업 알림 보내기 */
        alarmService.generateLevelAlarm(user, Action.POST);

        User postOwner = userRepository.getById(
                postRepository.getById(postId).getUser().getId()
        );

        /* 알림 생성 */
        Alarm alarm = Alarm.builder()
                .userId(postOwner.getId())
                .type(AlarmType.REPLY)
                .postId(postId)
                .isRead(false)
                .alarmMessage("[알림] ["
                            + postRepository.getById(postId).getTitle()
                            + "] 게시글에 잽이 등록되었습니다. 확인해보세요.")
                .build();

        /* 알림 메시지를 보낼 DTO 생성 */
        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .alarmId(alarm.getId())
                .type(alarm.getType().toString())
                .message("[알림] ["
                        + postRepository.getById(postId).getTitle()
                        + "] 게시글에 잽이 등록되었습니다. 확인해보세요.")
                .alarmTargetId(postOwner.getId())
                .isRead(alarm.getIsRead())
                .postId(alarm.getPostId())
                .build();

        /*-
         * redis로 알림메시지 pub, alarmRepository에 저장
         * 단, 게시글 작성자와 댓글 작성자가 일치할 경우는 제외
         */
        if (!alarmResponseDto.getAlarmTargetId().equals(user.getId())) {
            alarmRepository.save(alarm);
            redisTemplate.convertAndSend(channelTopic.getTopic(),
                    alarmResponseDto);
        }
        userRepository.save(user);

        return new PostCommentDto(
                userDetails.getUser().getId(),
                userDetails.getUser().getNickname(),
                comment.getId(),
                content,
                TimeConversion.timeConversion(comment.getCreatedAt()),
                userDetails.getUser().getTotalCount(),
                0L,
                userDetails.getUser().getMbti(),
                false,
                comment.getUser().getProfileImg().getProfileImgUrl(),
                userDetails.getUser().getLevel()
        );
    }

    /* 잽 삭제 */
    @Transactional
    public void deleteComment(long commentId, User user) {
        commentRepository.deleteById(commentId);
        user.minusTotalPostsAndComments();

        /* Level down */
        alarmService.generateLevelAlarm(user, Action.DELETE);
        userRepository.save(user);
    }

    /* 잽 좋아요 */
    @Transactional
    public ShowCommentDto likeComment(long commentId,
                                      UserDetailsImpl userDetails) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Post post = comment.get().getPost();

        /*-
         * 댓글 좋아요 하는 사람이 게시글 작성자라면
         * 댓글에 작성자에게 선택되었다고 체크
         */
        if (post.getUser().getId().equals(userDetails.getUser().getId())) {
            comment.get().selectedByPostOwner();
            commentRepository.save(comment.get());
        }
        /* 이미 좋아요 했으면 좋아요 취소 */
        CommentLike commentLike = commentLikeRepository
                .findByUserIdAndComment(userDetails.getUser().getId(),
                        comment.get());

        if (commentLike != null) {
            commentLikeRepository.delete(commentLike);
        } else {
            /* 안했으면 좋아요 + 1 */
            commentLikeRepository.save(
                    CommentLike.builder()
                            .userId(userDetails.getUser().getId())
                            .comment(commentRepository.getById(commentId))
                            .build());
        }

        return new ShowCommentDto(
                comment.get().getUser().getNickname(),
                comment.get().getUser().getLevel(),
                comment.get().getUser().getMbti(),
                comment.get().getId(),
                comment.get().getComment(),
                TimeConversion.timeConversion(comment.get().getCreatedAt()),
                commentLikeRepository
                        .findAllByComment(comment.get()).size(),
                commentLikeRepository.existsByCommentAndUserId(comment.get(),
                                userDetails.getUser().getId()),
                comment.get().getUser().getProfileImg().getProfileImgUrl()
        );
    }
}