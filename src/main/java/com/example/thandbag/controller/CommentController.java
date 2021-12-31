package com.example.thandbag.controller;

import com.example.thandbag.dto.PostCommentDto;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @CrossOrigin("*")
    @PostMapping("/api/{postId}/newComment")
    public PostCommentDto postComment(@PathVariable long postId, @RequestBody String comment, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.postComment(postId, comment, userDetails);
    }

    @CrossOrigin("*")
    @DeleteMapping("/api/uncomment/{commentId}")
    public void deleteComment(@PathVariable long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails);
    }

    @CrossOrigin("*")
    @PostMapping("/api/{commentId}/like")
    public void likeComment(@PathVariable long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.likeComment(commentId, userDetails);
    }
}
