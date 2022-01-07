package com.example.thandbag.timeconversion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeConversion {
    // 게시글 작성시 작성시간 포맷 변환
    public static String timeConversion(LocalDateTime modifiedAt) {
        LocalDateTime currentTime = LocalDateTime.now();
        Long timeDiff = Duration.between(modifiedAt, currentTime).getSeconds();
        String resultConversion = "";

        if ((timeDiff / 60 / 60 / 24) > 0) { // 일
            resultConversion = timeDiff / 60 / 60 / 24 + "일 전";
        } else if ((timeDiff / 60 / 60) > 0) { // 시간
            resultConversion = timeDiff / 60 / 60 + "시간 전";
        } else if ((timeDiff / 60) > 0) { // 분
            resultConversion = timeDiff / 60 + "분 전";
        } else {
            resultConversion = timeDiff + "초 전";
        }

        return resultConversion;
    }

    // 채팅 시간 '오전, 오후' 표시
    public static String ampmConversion(LocalDateTime createdAt) {
//        LocalDateTime currentTime = LocalDateTime.now();
        String resultConversion = createdAt.format(DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.forLanguageTag("en")));

        return resultConversion;
    }
}