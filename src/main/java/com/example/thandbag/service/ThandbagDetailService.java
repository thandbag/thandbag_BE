package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.dto.alarm.AlarmResponseDto;
import com.example.thandbag.dto.comment.ShowCommentDto;
import com.example.thandbag.dto.post.BestUserDto;
import com.example.thandbag.dto.post.PunchThangbagResponseDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThandbagDetailService {

    private final PostRepository postRepository;
    private final LvImgRepository lvImgRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    // 샌드백 상세 가져오기
    public ThandbagResponseDto getOneThandbag(long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("작성된 게시물이 없습니다"));
        List<String> imgUrlList = new ArrayList<>();
        for (PostImg postImg : post.getImgList())
            imgUrlList.add(postImg.getPostImgUrl());

        List<ShowCommentDto> showCommentDtoList = new ArrayList<>();
        // 게시글에 달린 댓글 가져오기
        for (Comment comment : post.getCommentList()) {
            List<CommentLike> allLikes = commentLikeRepository.findAllByComment(comment);
            ShowCommentDto showCommentDto = new ShowCommentDto(
                    comment.getUser().getNickname(),
                    comment.getUser().getLevel(),
                    comment.getUser().getMbti(),
                    comment.getId(),
                    comment.getComment(),
                    TimeConversion.timeConversion(comment.getCreatedAt()),
                    allLikes.size(),
                    commentLikeRepository.existsByCommentAndUserId(comment, user.getId()),
                    comment.getUser().getProfileImg().getProfileImgUrl()
            );
            showCommentDtoList.add(showCommentDto);
        }

        //얼빡배너
        String bannerLv = lvImgRepository.findByTitleAndLevel("얼빡배너 기본", post.getUser().getLevel()).getLvImgUrl();

        return ThandbagResponseDto.builder()
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .level(post.getUser().getLevel())
                .mbti(post.getUser().getMbti())
                .category(post.getCategory().getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrl(imgUrlList)
                .profileImgUrl(post.getUser().getProfileImg().getProfileImgUrl())
                .lvImg(bannerLv)
                .hitCount(post.getTotalHitCount())
                .createdAt(TimeConversion.timeConversion(post.getCreatedAt()))
                .share(post.getShare())
                .comments(showCommentDtoList)
                .totalCount(post.getUser().getTotalCount())
                .closed(post.getClosed())
                .commentCount(showCommentDtoList.size())
                .build();
    }

    // 생드백 삭제
    @Transactional
    public void removeThandbag(long postId, UserDetailsImpl userDetails) {
        postRepository.deleteById(postId);
        User user = userRepository.getById(userDetails.getUser().getId());
        user.setTotalCount(user.getTotalCount()-1);

        //leveldown 및 알림 메시지
        int totalPosts = user.getTotalCount();
        if(totalPosts <= 2 && user.getLevel() == 2) {
            user.setLevel(1);
            Alarm levelDown1 = Alarm.builder()
                    .userId(user.getId())
                    .type(AlarmType.LEVELCHANGE)
                    .alarmMessage("레벨이 " + user.getLevel() + "로 하락하였습니다.")
                    .isRead(false)
                    .build();

            alarmRepository.save(levelDown1);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelDown1.getId())
                    .type(levelDown1.getType().toString())
                    .message("[알림] 레벨이 " + user.getLevel() + "로 하락하였습니다.")
                    .alarmTargetId(user.getId())
                    .isRead(levelDown1.getIsRead())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        } else if(totalPosts < 5 && user.getLevel() == 3) {
            user.setLevel(2);
            Alarm levelDown2 = Alarm.builder()
                    .userId(user.getId())
                    .type(AlarmType.LEVELCHANGE)
                    .alarmMessage("레벨이 " + user.getLevel() + "로 하락하였습니다.")
                    .isRead(false)
                    .build();

            alarmRepository.save(levelDown2);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelDown2.getId())
                    .type(levelDown2.getType().toString())
                    .message("[알림] 레벨이 " + user.getLevel() + "로 하락하였습니다.")
                    .alarmTargetId(user.getId())
                    .isRead(levelDown2.getIsRead())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        }
    }

    //게시자가 작성한 생드백을 완료로 전환
    public List<BestUserDto> completeThandbag(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 없습니다"));
        post.closePost();
        postRepository.save(post);
        List<BestUserDto> bestUserDtoList = new ArrayList<>();
        // 게시자에게 선정된(게시자가 like한 댓글 작성자)를 선별
        for (Comment comment : post.getCommentList()) {
            //생드백 작성자에게 선택된 댓글 이면서, 생드백 작성자가 직접 쓴 잽이 아니라면
            if (comment.getLikedByWriter() && !comment.getUser().getId().equals(post.getUser().getId())) {
                BestUserDto bestUserDto = new BestUserDto(
                        comment.getUser().getId(),
                        comment.getUser().getMbti(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfileImg().getProfileImgUrl(),
                        comment.getUser().getLevel()
                );
                bestUserDtoList.add(bestUserDto);

                // 알림 생성
                //선택된 댓글 작성자들에게 알림 발송
                Alarm alarm = Alarm.builder()
                        .userId(comment.getUser().getId())
                        .type(AlarmType.PICKED)
                        .postId(comment.getPost().getId())
                        .alarmMessage("[" + post.getTitle()  + "] 생드백에서 내 잽이 베스트 잽으로 선정되었습니다.")
                        .isRead(false)
                        .build();

                alarmRepository.save(alarm);

                // 알림 메시지를 보낼 DTO 생성
                AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                        .alarmId(alarm.getId())
                        .type(alarm.getType().toString())
                        .postId(alarm.getPostId())
                        .message("[알림] [" + post.getTitle()  + "] 생드백에서 내 잽이 베스트 잽으로 선정되었습니다.")
                        .alarmTargetId(alarm.getUserId())
                        .isRead(alarm.getIsRead())
                        .build();
                // redis로 pub
                redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
            }
        }
        return bestUserDtoList;
    }

    // 생드백 맞은수 업데이트
    public void updateTotalPunch(Long postId, int totalHitCount) {
        Post post = postRepository.getById(postId);
        post.updateTotalHit(totalHitCount);
        postRepository.save(post);
    }

    //현재까지 맞은 수와 함께 생드백 불러오기(샌드백 때리기 페이지로 이동하기)
    public PunchThangbagResponseDto getpunchedThandBag(Long postId, User user) {
        Post post = postRepository.getById(postId);
        boolean ownThangBag = user.getId().equals(post.getUser().getId());
        return new PunchThangbagResponseDto(post.getTotalHitCount(), ownThangBag);
    }
}
