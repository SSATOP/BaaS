package com.baas.securities.enums;

public enum TickerDummy {
    SS("005930"),SK("000660");
    private String name;

    TickerDummy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TickerDummy getCode(String name){
        for(TickerDummy t : TickerDummy.values()){
            if(t.getName().equals(name)){
                return t;
            }
        }
        return null;
    }
}
