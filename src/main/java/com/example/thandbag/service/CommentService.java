package com.example.thandbag.service;

import com.example.thandbag.dto.PostCommentDto;
import com.example.thandbag.model.Comment;
import com.example.thandbag.model.CommentLike;
import com.example.thandbag.model.Post;
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
        postRepository.getById(postId).getCommentList().add(comment);
        comment = commentRepository.save(comment);

        // 전체 게시글 수 count
        User user = userDetails.getUser();
        user.plusTotalPostsAndComments();
        userRepository.save(user);

        //levelup
        int totalPosts = user.getTotalCount();
        if(totalPosts < 5 && totalPosts > 2 && user.getLevel() == 1)
            user.setLevel(2);
        else if(totalPosts >= 5)
            user.setLevel(3);

        // levelup 으로인한 알림 안함

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

        //leveldown
        int totalPosts = user.getTotalCount();
        if(totalPosts <= 2 && user.getLevel() == 2)
            user.setLevel(1);
        else if(totalPosts < 5 && user.getLevel() == 3)
            user.setLevel(2);

        // leveldown 으로인한 알림 안함
    }

    @Transactional
    public void likeComment(long commentId, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.getById(commentId);
        Post post = comment.getPost();
        if(post.getUser().getId().equals(userDetails.getUser().getId()))
            comment.selectedByPostOwner();
        commentRepository.save(comment);
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
