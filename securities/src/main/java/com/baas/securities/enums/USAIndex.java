package com.baas.securities.enums;

public enum USAIndex {
    SP500("SPX", "S&P500"), DOW(".DJI", "다우지수"), NASDAQ("NDX", "나스닥");

    private String code;
    private String indexName;

    USAIndex(String code, String indexName) {
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
