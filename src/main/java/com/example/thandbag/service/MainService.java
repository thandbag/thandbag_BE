package com.example.thandbag.service;

import com.example.thandbag.Enum.AlarmType;
import com.example.thandbag.Enum.Category;
import com.example.thandbag.dto.AlarmResponseDto;
import com.example.thandbag.dto.ThandbagRequestDto;
import com.example.thandbag.dto.ThandbagResponseDto;
import com.example.thandbag.model.Alarm;
import com.example.thandbag.model.Post;
import com.example.thandbag.model.PostImg;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.*;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.timeconversion.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
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
    private final AlarmRepository alarmRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    // 샌드백 생성
    @Transactional
    public ThandbagResponseDto createThandbag(ThandbagRequestDto thandbagRequestDto, UserDetailsImpl userDetails) {

        // 헌재 사용자 가져오기
        User user = userDetails.getUser();
        List<PostImg> postImgList = new ArrayList<>();
        List<String> fileUrlList = new ArrayList<>();

        // 이미지 올렸으면 저장 처리하기
        if (thandbagRequestDto.getImgUrl() != null) {
            for (MultipartFile multipartFile : thandbagRequestDto.getImgUrl()) {
                String imgUrl = postService.uploadFile(multipartFile);
                PostImg img = new PostImg(imgUrl);
                fileUrlList.add(imgUrl);
                postImgList.add(img);
            }
        }

        // 글 저장
        Post post = Post.builder()
                .title(thandbagRequestDto.getTitle())
                .category(Category.valueOf(thandbagRequestDto.getCategory()))
                .closed(false)
                .content(thandbagRequestDto.getContent())
                .imgList(postImgList)
                .share(thandbagRequestDto.isShare())
                .user(user)
                .build();
        postRepository.save(post);

        //전체 게시글 수 count
        user.plusTotalPostsAndComments();

        //levelup
        int totalPosts = user.getTotalCount();
        if (totalPosts < 5 && totalPosts > 2 && user.getLevel() == 1) {
            user.setLevel(2);
            // 레벨업 알림 생성
            Alarm levelAlarm2 = new Alarm(
                    user.getId(),
                    AlarmType.LEVELCHANGE,
                    "레벨이 상승하였습니다."
            );
            alarmRepository.save(levelAlarm2);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelAlarm2.getId())
                    .type(levelAlarm2.getType().toString())
                    .message("[알림] 레벨이 상승하였습니다.")
                    .alarmTargetId(user.getId())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        } else if (totalPosts >= 5) {
            user.setLevel(3);

            // 레벨업 알림 생성
            Alarm levelAlarm3 = new Alarm(
                    user.getId(),
                    AlarmType.LEVELCHANGE,
                    "레벨이 상승하였습니다."
            );
            alarmRepository.save(levelAlarm3);

            // 알림 메시지를 보낼 DTO 생성
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(levelAlarm3.getId())
                    .type(levelAlarm3.getType().toString())
                    .message("[알림] 레벨이 상승하였습니다.")
                    .alarmTargetId(user.getId())
                    .build();

            redisTemplate.convertAndSend(channelTopic.getTopic(), alarmResponseDto);
        }

        user = userRepository.save(user);
        Post posted = postRepository.findById(post.getId()).orElseThrow(
                () -> new NullPointerException("post가 없습니다"));

        //얼빡배너 lv
        String bannerLv = lvImgRepository.findByTitleAndLevel("얼빡배너 기본", user.getLevel()).getLvImgUrl();

        return ThandbagResponseDto.builder()
                .userId(user.getId())
                .category(thandbagRequestDto.getCategory())
                .createdAt(TimeConversion.timeConversion(posted.getCreatedAt()))
                .share(thandbagRequestDto.isShare())
                .imgUrl(fileUrlList)
                .totalCount(user.getTotalCount())
                .content(thandbagRequestDto.getContent())
                .nickname(user.getNickname())
                .level(user.getLevel())
                .mbti(user.getMbti())
                .lvImg(bannerLv)
                .closed(posted.getClosed())
                .share(posted.getShare())
                .hitCount(posted.getTotalHitCount())
                .totalCount(user.getTotalCount())
                .title(thandbagRequestDto.getTitle())
                .build();
    }

    //샌드백 전체 리스트 페이지로 만들기
    public List<ThandbagResponseDto> showAllThandbag(int page, int size) {
        Pageable sortedByModifiedAtDesc = PageRequest.of(page, size, Sort.by("modifiedAt").descending());
        List<ThandbagResponseDto> allThandbags = new ArrayList<>();
        List<Post> allPosts = postRepository.findAllByShareTrueOrderByCreatedAtDesc(sortedByModifiedAtDesc).getContent();
        for (Post post : allPosts) {
            ThandbagResponseDto thandbagResponseDto = createThandbagResponseDto(post);
            allThandbags.add(thandbagResponseDto);
        }
        return allThandbags;
    }

    //검색된 샌드백 전체 리스트 페이지로 만들기
    public List<ThandbagResponseDto> searchThandbags(String keyword, int pageNumber, int size) {
        List<Post> posts = postRepository.findAllByShareTrueOrderByCreatedAtDesc();
        // 키워드가 유저 닉네임, 타이틀, 또는 게시글 내용에 포함되지 않았으면 삭제
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

    // 검색된 샌드백 또는 샌드백 전체 리스트의 dto 작성을 위한 helping function
    public ThandbagResponseDto createThandbagResponseDto(Post post) {
        List<PostImg> postImgList = postImgRepository.findAllByPostId(post.getId());
        List<String> imgList = new ArrayList<>();
        for (PostImg postImg : postImgList)
            imgList.add(postImg.getPostImgUrl());
        return ThandbagResponseDto.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                //ispresent check 안했음
                .level(post.getUser().getLevel())
                .title(post.getTitle())
                .category(post.getCategory().getCategory())
                .createdAt(TimeConversion.timeConversion(post.getCreatedAt()))
                .closed(post.getClosed())
                .mbti(post.getUser().getMbti())
                .commentCount(post.getCommentList().size())
                .totalCount(post.getUser().getTotalCount())
                .hitCount(post.getTotalHitCount())
                .content(post.getContent())
                .imgUrl(imgList)
                .share(post.getShare())
                .closed(post.getClosed())
                .build();
    }
}
