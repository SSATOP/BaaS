package com.baas.securities.enums;

public enum Period {
    DAY("D"),
    WEEK("W"),
    MONTH("M"),
    YEAR("Y"),
    ;

    private final String keyword;

    Period(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
