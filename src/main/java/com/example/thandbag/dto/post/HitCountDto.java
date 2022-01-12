package com.example.thandbag.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HitCountDto {

    private int prevHitCount;
    private int newHitCount;
}
