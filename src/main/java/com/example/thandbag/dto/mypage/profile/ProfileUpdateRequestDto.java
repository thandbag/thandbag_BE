package com.example.thandbag.dto.mypage.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequestDto {

    private String nickname;
    private String mbti;
}
