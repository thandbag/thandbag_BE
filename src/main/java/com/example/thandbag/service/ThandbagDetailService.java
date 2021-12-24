package com.example.thandbag.service;

import com.example.thandbag.dto.BestUserDto;
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
    private final CommentRepository commentRepository;
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
                    lvImgRepository.getById(comment.getUser().getLvImgId()).getLvImgUrl(),
                    comment.getUser().getMbti(),
                    comment.getComment(),
                    TimeConversion.timeConversion(comment.getCreatedAt()),
                    allLikes.size()
            );
            showCommentDtoList.add(showCommentDto);
        }
        return ThandbagResponseDto.builder()
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .lvIcon(lvImgRepository.getById(post.getUser().getLvImgId()).getLvImgUrl())
                .mbti(post.getUser().getMbti())
                .category(post.getCategory().getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrl(imgUrlList)
                .createdAt(TimeConversion.timeConversion(post.getCreatedAt()))
                .share(post.getShare())
                .comments(showCommentDtoList)
                .build();
    }

    @Transactional
    public void removeThandbag(long postId, UserDetailsImpl userDetails) {
        postRepository.deleteById(postId);
        User user = userRepository.getById(userDetails.getUser().getId());
        user.minusTotalPostsAndComments();

        //leveldown 재조정 여부 아직 안함

        // leveldown 으로인한 알림 안함
    }

    @Transactional
    public List<BestUserDto> completeThandbag(long postId, List<Long> commentIdList) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 없습니다"));
        post.closePost();

        List<BestUserDto> bestUserDtoList = new ArrayList<>();
        for(long commentId : commentIdList) {
            Comment comment = commentRepository.getById(commentId);
            comment.selectedByPostOwner();
            commentRepository.save(comment);
            BestUserDto bestUserDto = new BestUserDto(
                    comment.getUser().getId(),
                    comment.getUser().getMbti(),
                    comment.getUser().getNickname());
            bestUserDtoList.add(bestUserDto);
        }
        return bestUserDtoList;
    }
}
