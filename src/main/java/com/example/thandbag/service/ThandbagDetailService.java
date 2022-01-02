package com.example.thandbag.service;

import com.example.thandbag.dto.BestUserDto;
import com.example.thandbag.dto.PunchThangbagResponseDto;
import com.example.thandbag.dto.ShowCommentDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.model.*;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
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

    public ThandbagResponseDto getOneThandbag(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("작성된 게시물이 없습니다"));
        List<String> imgUrlList = new ArrayList<>();
        for (PostImg postImg : post.getImgList())
            imgUrlList.add(postImg.getPostImgUrl());

        List<ShowCommentDto> showCommentDtoList = new ArrayList<>();
        for (Comment comment : post.getCommentList()) {
            List<CommentLike> allLikes = commentLikeRepository.findAllByUserId(comment.getUser().getId());
            ShowCommentDto showCommentDto = new ShowCommentDto(
                    comment.getUser().getNickname(),
                    comment.getUser().getLevel(),
                    comment.getUser().getMbti(),
                    comment.getComment(),
                    TimeConversion.timeConversion(comment.getCreatedAt()),
                    allLikes.size()
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

    @Transactional
    public void removeThandbag(long postId, UserDetailsImpl userDetails) {
        postRepository.deleteById(postId);
        User user = userRepository.getById(userDetails.getUser().getId());
        user.minusTotalPostsAndComments();

        //leveldown
        int totalPosts = user.getTotalCount();
        if(totalPosts <= 2 && user.getLevel() == 2)
            user.setLevel(1);
        else if(totalPosts < 5 && user.getLevel() == 3)
            user.setLevel(2);
        // leveldown 으로인한 알림 안함
    }

    public List<BestUserDto> completeThandbag(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 없습니다"));
        post.closePost();
        postRepository.save(post);
        List<BestUserDto> bestUserDtoList = new ArrayList<>();
        for (Comment comment : post.getCommentList()) {
            //게시글 작성자에게 선택된 댓글 이면서 댓글이 작성자가 직접 단경우가 아니라면
            if (comment.getLikedByWriter() && !comment.getUser().getId().equals(post.getUser().getId())) {
                BestUserDto bestUserDto = new BestUserDto(
                        comment.getUser().getId(),
                        comment.getUser().getMbti(),
                        comment.getUser().getNickname());
                bestUserDtoList.add(bestUserDto);
            }
        }
        return bestUserDtoList;
    }

    public void updateTotalPunch(Long postId, int totalHitCount) {
        Post post = postRepository.getById(postId);
        post.updateTotalHit(totalHitCount);
        postRepository.save(post);
    }

    public PunchThangbagResponseDto getpunchedThandBag(Long postId, User user) {
        Post post = postRepository.getById(postId);
        boolean ownThangBag = user.getId().equals(post.getUser().getId());
        return new PunchThangbagResponseDto(post.getTotalHitCount(), ownThangBag);
    }



}
