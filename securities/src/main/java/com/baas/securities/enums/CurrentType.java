package com.baas.securities.enums;

public enum CurrentType {
    WON("원화"),DOLLAR("달러");

    private String name;

    CurrentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CurrentType getCode(String name){
        for(CurrentType code : CurrentType.values()) {
            if (code.getName().equals(name)) {
                return code;
            }
        }
        return null;
    }
}
