package com.example.thandbag.service;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.ThandbagRequestDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.LvImgRepository;
import com.example.thandbag.repository.PostImgRepository;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MainService {

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final UserRepository userRepository;
    private final LvImgRepository lvImgRepository;

    @Transactional
    public ThandbagResponseDto createThandbag(ThandbagRequestDto thandbagRequestDto) {

        User user = new User();
        Post post = Post.builder()
                .title(thandbagRequestDto.getTitle())
                .category(Category.valueOf(thandbagRequestDto.getCategory()))
                .closed(thandbagRequestDto.isShare())
                .content(thandbagRequestDto.getContent())
                .share(thandbagRequestDto.isShare())
                .user(user)
                .build();
        postRepository.save(post);
        user.updateTotalPostsAndComments();
        userRepository.save(user);
        Post posted = postRepository.findById(post.getId()).orElseThrow(
                () -> new NullPointerException("post가 없습니다"));

        return ThandbagResponseDto.builder()
                .userId(user.getId())
                .category(thandbagRequestDto.getCategory())
                .createdAt(TimeConversion.timeConversion(posted.getCreatedAt()))
                .share(thandbagRequestDto.isShare())
                .imgUrl(thandbagRequestDto.getImgUrl())
                .totalCount(user.getTotalCount())
                .content(thandbagRequestDto.getContent())
                .nickname(user.getNickname())
                .title(thandbagRequestDto.getTitle())
                .build();
    }

    public List<ThandbagResponseDto> showAllThandbag(int page, int size) {
        Pageable sortedByModifiedAtDesc = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        List<ThandbagResponseDto> allThandbags = new ArrayList<>();
        List<Post> allPosts = postRepository.findAllByOrderByCreatedAtDesc(sortedByModifiedAtDesc).getContent();
        for(Post post : allPosts) {
            ThandbagResponseDto thandbagResponseDto = ThandbagResponseDto.builder()
                    .userId(post.getUser().getId())
                    .nickname(post.getUser().getNickname())
                    //ispresent check 안했음
                    .lvIcon(lvImgRepository.findById(post.getUser().getLvImgId()).get().getLvImgUrl())
                    .title(post.getTitle())
                    .category(post.getCategory().getCategory())
                    .createdAt(TimeConversion.timeConversion(post.getCreatedAt()))
                    .closed(post.getClosed())
                    .mbti(post.getUser().getMbti())
                    .commentCount(post.getCommentList().size())
                    .content(post.getContent())
                    .imgUrl(postImgRepository.findByPostId(post.getId()).getPostImgUrl())
                    .build();
            allThandbags.add(thandbagResponseDto);
        }
        return allThandbags;
    }

    public List<ThandbagResponseDto> searchThandbags(String keyword, int pageNumber, int size) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        posts.removeIf(post -> !(userRepository.getById(post.getUser().getId()).getNickname().contains(keyword)
                || post.getContent().contains(keyword) || post.getTitle().contains(keyword)));
        PagedListHolder<Post> page = new PagedListHolder<>(posts);
        page.setPageSize(size);
        page.setPage(pageNumber);
        posts = page.getPageList();
        List<ThandbagResponseDto> searchedPosts = new ArrayList<>();
        for (Post post : posts) {
            ThandbagResponseDto thandbagResponseDto = ThandbagResponseDto.builder()
                    .userId(post.getUser().getId())
                    .nickname(post.getUser().getNickname())
                    //ispresent check 안했음
                    .lvIcon(lvImgRepository.findById(post.getUser().getLvImgId()).get().getLvImgUrl())
                    .title(post.getTitle())
                    .category(post.getCategory().getCategory())
                    .createdAt(TimeConversion.timeConversion(post.getCreatedAt()))
                    .closed(post.getClosed())
                    .mbti(post.getUser().getMbti())
                    .commentCount(post.getCommentList().size())
                    .content(post.getContent())
                    .imgUrl(postImgRepository.findByPostId(post.getId()).getPostImgUrl())
                    .build();
            searchedPosts.add(thandbagResponseDto);
        }
        return searchedPosts;
    }
}
