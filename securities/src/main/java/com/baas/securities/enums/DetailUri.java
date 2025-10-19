package com.baas.securities.enums;

public enum DetailUri {
    KOR_INDEX("/uapi/domestic-stock/v1/quotations/inquire-index-price"),
    USA_INDEX("/uapi/overseas-price/v1/quotations/inquire-daily-chartprice"),
    KOR_STOCK_VOLUME_RANK("/uapi/domestic-stock/v1/quotations/volume-rank");

    private String name;

    DetailUri(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
