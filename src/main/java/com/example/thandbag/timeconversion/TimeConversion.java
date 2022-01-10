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
        String resultConversion = createdAt.format(DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.forLanguageTag("en")));

        return resultConversion;
    }

    // 채팅 리스트에서 보여줄 시간
    public static String chattingListTimeConversion(LocalDateTime lastContentTime){
        String resultConversion = "";
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
        // 오늘 날짜에 대한 년/월/일
        int yearToday = today.getYear();
        int monthToday = today.getMonthValue();
        int dayToday = today.getDayOfMonth();

        // 컨텐츠의 날짜에 대한 년/월/일
        int yearContent = lastContentTime.getYear();
        int monthContent = lastContentTime.getMonthValue();
        int dayContent = lastContentTime.getDayOfMonth();

        // 컨텐츠 날짜가 오늘이면 오전/오후/시간/분으로 보내주기
        if (yearToday == yearContent && monthToday == monthContent && dayToday == dayContent) {
            resultConversion = lastContentTime.format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.forLanguageTag("ko")));
        } else if(yearToday == yearContent && monthToday == monthContent && (dayToday - dayContent == 1)) {
            resultConversion = "어제";
        } else {
            resultConversion = lastContentTime.format(formatter);
        }

        return resultConversion;
    }
}