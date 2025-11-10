package com.baas.securities.enums;

public enum KorIndex {
    KOSPI("0001", "코스피"), KOSDAQ("1001", "코스닥"), KOSPI200("2001", "코스피200");

    private String code;
    private String indexName;

    KorIndex(String code, String indexName) {
        this.code = code;
        this.indexName = indexName;
    }

    public String getCode() {
        return code;
    }

    public String getIndexName() {
        return indexName;
    }
}
