package com.example.thandbag.dto.mypage.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequestDto {

    private String nickname;
    private MultipartFile file;
    private String mbti;

}
