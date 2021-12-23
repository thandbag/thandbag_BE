package com.example.thandbag.service;

import com.example.thandbag.dto.ShowCommentDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.PostImg;
import com.example.thandbag.repository.CommentLikeRepository;
import com.example.thandbag.repository.LvImgRepository;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThandbagDetailService {

    private final PostRepository postRepository;
    private final LvImgRepository lvImgRepository;
    private final CommentLikeRepository commentLikeRepository;

    public ThandbagResponseDto getOneThandbag(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("작성된 게시물이 없습니다"));
        List<String> imgUrlList = new ArrayList<>();
        for (PostImg postImg : post.getImgList())
            imgUrlList.add(postImg.getPostImgUrl());

        List<ShowCommentDto> showCommentDtoList = new ArrayList<>();
        for (Comment comment : post.getCommentList()) {
            CommentLike commentLike = commentLikeRepository.findByComment(comment).orElse(new CommentLike());
            ShowCommentDto showCommentDto = new ShowCommentDto(
                    comment.getUser().getNickname(),
                    lvImgRepository.getById(comment.getUser().getLvImgId()).getLvImgUrl(),
                    comment.getUser().getMbti(),
                    comment.getComment(),
                    TimeConversion.timeConversion(comment.getCreatedAt()),
                    commentLike.getTotalLike()
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

    public void removeThandbag(long postId) {
        postRepository.deleteById(postId);
    }
}
