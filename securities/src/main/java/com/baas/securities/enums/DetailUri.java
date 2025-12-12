package com.baas.securities.enums;

public enum DetailUri {
    KOR_INDEX("/uapi/domestic-stock/v1/quotations/inquire-index-price"),
    USA_INDEX("/uapi/overseas-price/v1/quotations/inquire-daily-chartprice"),
    KOR_STOCK_VOLUME_RANK("/uapi/domestic-stock/v1/quotations/volume-rank"),
    KOR_STOCK_MARKET_CAPITALIZATION_RANK("/uapi/domestic-stock/v1/ranking/market-cap"),
    KOR_PERIOD_STOCK("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice");

    private String name;

    DetailUri(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
