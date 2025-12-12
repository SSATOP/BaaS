package com.baas.securities.enums;

public enum Ticker {
    SS("005930"),SK("000660");
    private String name;

    Ticker(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Ticker getCode(String name){
        for(Ticker t : Ticker.values()){
            if(t.getName().equals(name)){
                return t;
            }
        }
        return null;
    }
}
