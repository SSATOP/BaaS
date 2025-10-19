package com.baas.securities.enums;

public enum TrId {
    KOR_INDEX("FHPUP02100000"), KOR_STOCK_VOLUME_RANK("FHPST01710000"), USA_INDEX("FHKST03030100");

    private String name;

    TrId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
