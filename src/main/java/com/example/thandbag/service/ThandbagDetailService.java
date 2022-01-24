package com.example.thandbag.service;

import com.example.thandbag.Enum.Action;
import com.example.thandbag.dto.comment.ShowCommentDto;
import com.example.thandbag.dto.post.BestUserDto;
import com.example.thandbag.dto.post.HitCountDto;
import com.example.thandbag.dto.post.PunchThangbagResponseDto;
import com.example.thandbag.dto.post.ThandbagResponseDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThandbagDetailService {

    private final AlarmService alarmService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LvImgRepository lvImgRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final AlarmRepository alarmRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    /* 생드백 상세 가져오기 */
    public ThandbagResponseDto getOneThandbag(long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("작성된 게시물이 없습니다"));
        List<String> imgUrlList = new ArrayList<>();
        for (PostImg postImg : post.getImgList())
            imgUrlList.add(postImg.getPostImgUrl());

        List<ShowCommentDto> showCommentDtoList = new ArrayList<>();

        /* 게시글에 달린 댓글 가져오기 */
        for (Comment comment : post.getCommentList()) {
            List<CommentLike> allLikes = comment.getCommentLikeList();

            /* login 한 유저, 안한 유저 구분 */
            Boolean likeExist = user.getNickname() != "visitor"
                            ? commentLikeRepository
                                .existsByCommentAndUserId(comment, user.getId())
                            : false;

            ShowCommentDto showCommentDto = new ShowCommentDto(
                    comment.getUser().getNickname(),
                    comment.getUser().getLevel(),
                    comment.getUser().getMbti(),
                    comment.getId(),
                    comment.getComment(),
                    TimeConversion.timeConversion(comment.getCreatedAt()),
                    allLikes.size(),
                    likeExist,
                    comment.getUser().getProfileImg().getProfileImgUrl()
            );

            showCommentDtoList.add(showCommentDto);
        }

        /* 얼빡배너가 상태에 따라 변화함 */
        String lvImgTitle = "얼빡배너 기본";

        if (!post.getClosed()) {
            /* TotalHitCount에 따라 변경됨 */
            if (post.getTotalHitCount() >= 10) {
                lvImgTitle = "얼빡배너 쳐맞음";
            }
        } else {
            lvImgTitle = "얼빡배너 터짐";
        }


        String bannerLv = lvImgRepository.findByTitleAndLevel(
                lvImgTitle,
                post.getUser().getLevel()
        ).getLvImgUrl();

        return ThandbagResponseDto.builder()
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .level(post.getUser().getLevel())
                .mbti(post.getUser().getMbti())
                .category(post.getCategory().getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrl(imgUrlList)
                .profileImgUrl(
                        post.getUser()
                        .getProfileImg()
                        .getProfileImgUrl()
                )
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

    /* 생드백 삭제 */
    @Transactional
    public void removeThandbag(long postId, User user) {
        postRepository.deleteById(postId);
        user.setTotalCount(user.getTotalCount() - 1);
        alarmRepository.deleteAllByPostId(postId);

        /* leveldown 및 알림 메시지 */
        alarmService.generateLevelAlarm(user, Action.DELETE);
        userRepository.save(user);
    }

    /* 게시자가 작성한 생드백을 완료로 전환 */
    public List<BestUserDto> completeThandbag(long postId,
                                              HitCountDto hitCountDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 없습니다"));
        post.updateTotalHit(hitCountDto);
        post.closePost();
        postRepository.save(post);

        List<BestUserDto> bestUserDtoList = new ArrayList<>();
        HashSet<String> commenterName = new HashSet<>();
        /* 게시자에게 선정된(게시자가 like한 댓글 작성자)를 선별 */
        for (Comment comment : post.getCommentList()) {
            /* 생드백 작성자에게 선택된 댓글 + 생드백 작성자가 쓴 잽이 아니라면 */
            if (comment.getLikedByWriter() &&
                    !comment.getUser().getId().equals(post.getUser().getId())) {
                BestUserDto bestUserDto = new BestUserDto(
                        comment.getUser().getId(),
                        comment.getUser().getMbti(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfileImg().getProfileImgUrl(),
                        comment.getUser().getLevel()
                );

                /* 한사람 댓글이 여러번 선택됬을경우 한번만 선택 */
                if (!commenterName.contains(bestUserDto.getNickname())) {
                    bestUserDtoList.add(bestUserDto);
                    commenterName.add(bestUserDto.getNickname());
                }

                /* 선택된 댓글 작성자들에게 알림 발송 */
                alarmService.generatePickedAlarm(post, comment);
            }
        }
        return bestUserDtoList;
    }

    /*-
     * 생드백 맞은수 업데이트
     * 프론트엔드에서 샌드백 터뜨리기를 진입했을때,
     * 때리기 전의 전체 hit수와 뒤로가기를 눌렀을때의 전체hit수를 보내주면 가능
     */
    @Transactional
    public void updateTotalPunch(Long postId, HitCountDto hitCountDto) {
        Optional<Post> post = postRepository.findByIdForHitCount(postId);
        post.get().updateTotalHit(hitCountDto);
        postRepository.save(post.get());
    }

    /* 현재까지 맞은 수와 함께 생드백 불러오기(샌드백 때리기 페이지로 이동) */
    public PunchThangbagResponseDto getpunchedThandBag(Long postId, User user) {
        Post post = postRepository.getById(postId);
        boolean ownThangBag = user.getId().equals(post.getUser().getId());

        return new PunchThangbagResponseDto(
                post.getTotalHitCount(),
                ownThangBag
        );
    }
}
