package com.example.thandbag.controller;

import com.example.thandbag.dto.comment.PostCommentDto;
import com.example.thandbag.dto.comment.ShowCommentDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 생드백에 댓글 달기
    @PostMapping("/api/{postId}/newComment")
    public PostCommentDto postComment(@PathVariable long postId, @RequestBody String comment, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.postComment(postId, comment, userDetails);
    }

    // 댓글 삭제하기
    @DeleteMapping("/api/uncomment/{commentId}")
    public void deleteComment(@PathVariable long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser());
    }

    // 댓글에 좋아요 누르기
    @PostMapping("/api/{commentId}/like")
    public ShowCommentDto likeComment(@PathVariable long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.likeComment(commentId, userDetails);
    }
}
