package com.example.thandbag.service;

import com.example.thandbag.dto.ThandbagResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ThandbagDetailService {
    public ThandbagResponseDto getOneThandbag(int postId, boolean share) {
        return new ThandbagResponseDto();
    }

    public void removeThandbag(int postId) {
    }
}
