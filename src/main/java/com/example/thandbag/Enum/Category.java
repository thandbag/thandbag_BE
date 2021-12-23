package com.example.thandbag.Enum;

public enum Category {
    SOCIAL("사회생활"),
    STUDY("공부"),
    JOB("진로고민"),
    PEOPLE("대인관계"),
    FAMILY("가정문제"),
    LOVE("연애"),
    OTHERS("기타");

    private final String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory(){
        return this.category;
    }
}
