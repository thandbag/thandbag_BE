package com.example.thandbag.service;

import com.example.thandbag.dto.PostCommentDto;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.CommentLikeRepository;
import com.example.thandbag.repository.CommentRepository;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public PostCommentDto postComment(long postId, String content, UserDetailsImpl userDetails) {
        Comment comment = Comment.builder()
                .comment(content)
                .likedByWriter(false)
                .user(userDetails.getUser())
                .post(postRepository.getById(postId))
                .build();
        comment = commentRepository.save(comment);

        // 전체 게시글 수 count
        User user = userDetails.getUser();
        user.plusTotalPostsAndComments();
        userRepository.save(user);

        //levelup여부 아직 구현 안됨

        return new PostCommentDto(
                userDetails.getUser().getId(),
                userDetails.getUser().getNickname(),
                content,
                TimeConversion.timeConversion(comment.getCreatedAt()),
                userDetails.getUser().getTotalCount()
        );
    }

    public void deleteComment(long commentId, UserDetailsImpl userDetails) {
        commentRepository.deleteById(commentId);
        User user = userRepository.getById(userDetails.getUser().getId());
        user.minusTotalPostsAndComments();

        //leveldown 재조정 여부 아직 안함

        // leveldown 으로인한 알림 안함
    }

    public void likeComment(long commentId, UserDetailsImpl userDetails) {
        if(commentLikeRepository.existsByUserId(userDetails.getUser().getId())) {
            commentLikeRepository.deleteById(commentId);
        } else {
            commentLikeRepository.save(
                    CommentLike.builder()
                    .userId(userDetails.getUser().getId())
                    .comment(commentRepository.getById(commentId))
                    .build());
        }
    }
}
