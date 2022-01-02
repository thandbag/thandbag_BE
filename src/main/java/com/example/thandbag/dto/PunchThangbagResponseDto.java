package com.example.thandbag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PunchThangbagResponseDto {
    private int totalHitCount;
    private boolean ownThandBag;
}
