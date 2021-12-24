package com.example.thandbag.service;

import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.ThandbagRequestDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.PostImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.LvImgRepository;
import com.example.thandbag.repository.PostImgRepository;
import com.example.thandbag.repository.PostRepository;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MainService {

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final UserRepository userRepository;
    private final LvImgRepository lvImgRepository;
    private final PostService postService;

    @Transactional
    public ThandbagResponseDto createThandbag(ThandbagRequestDto thandbagRequestDto, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        List<PostImg> postImgList = new ArrayList<>();
        List<String> fileUrlList = new ArrayList<>();
        if (thandbagRequestDto.getImgUrl() != null) {
            for (MultipartFile multipartFile : thandbagRequestDto.getImgUrl()) {
                String imgUrl = postService.uploadFile(multipartFile);
                PostImg img = new PostImg(imgUrl);
                fileUrlList.add(imgUrl);
                postImgList.add(img);
            }
        }

        Post post = Post.builder()
                .title(thandbagRequestDto.getTitle())
                .category(Category.valueOf(thandbagRequestDto.getCategory()))
                .closed(thandbagRequestDto.isShare())
                .content(thandbagRequestDto.getContent())
                .imgList(postImgList)
                .share(thandbagRequestDto.isShare())
                .user(user)
                .build();
        postRepository.save(post);

        //전체 게시글 수 count
        user.plusTotalPostsAndComments();
        userRepository.save(user);
        Post posted = postRepository.findById(post.getId()).orElseThrow(
                () -> new NullPointerException("post가 없습니다"));


        //levelup 여부 아직 구현 안됨

        //levelup 했으면 알림

        return ThandbagResponseDto.builder()
                .userId(user.getId())
                .category(thandbagRequestDto.getCategory())
                .createdAt(TimeConversion.timeConversion(posted.getCreatedAt()))
                .share(thandbagRequestDto.isShare())
                .imgUrl(fileUrlList)
                .totalCount(user.getTotalCount())
                .content(thandbagRequestDto.getContent())
                .nickname(user.getNickname())
                .title(thandbagRequestDto.getTitle())
                .build();
    }

    public List<ThandbagResponseDto> showAllThandbag(int page, int size) {
        Pageable sortedByModifiedAtDesc = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        List<ThandbagResponseDto> allThandbags = new ArrayList<>();
        List<Post> allPosts = postRepository.findAllByShareTrueOrderByCreatedAtDesc(sortedByModifiedAtDesc).getContent();
        for(Post post : allPosts) {
            ThandbagResponseDto thandbagResponseDto = createThandbagResponseDto(post);
            allThandbags.add(thandbagResponseDto);
        }
        return allThandbags;
    }

    public List<ThandbagResponseDto> searchThandbags(String keyword, int pageNumber, int size) {
        List<Post> posts = postRepository.findAllByShareTrueOrderByCreatedAtDesc();
        posts.removeIf(post -> !(userRepository.getById(post.getUser().getId()).getNickname().contains(keyword)
                || post.getContent().contains(keyword) || post.getTitle().contains(keyword)));
        PagedListHolder<Post> page = new PagedListHolder<>(posts);
        page.setPageSize(size);
        page.setPage(pageNumber);
        posts = page.getPageList();
        List<ThandbagResponseDto> searchedPosts = new ArrayList<>();
        for (Post post : posts) {
            ThandbagResponseDto thandbagResponseDto = createThandbagResponseDto(post);
            searchedPosts.add(thandbagResponseDto);
        }
        return searchedPosts;
    }

    public ThandbagResponseDto createThandbagResponseDto (Post post) {
        List<PostImg> postImgList = postImgRepository.findAllByPostId(post.getId());
        List<String> imgList = new ArrayList<>();
        for(PostImg postImg : postImgList)
            imgList.add(postImg.getPostImgUrl());

        return ThandbagResponseDto.builder()
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
                .imgUrl(imgList)
                .build();
    }
}
