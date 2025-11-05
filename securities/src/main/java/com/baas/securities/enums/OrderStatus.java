package com.baas.securities.enums;

import com.baas.securities.repository.entity.Order;

public enum OrderStatus {
    SUCCESS("성공"),FAIL("실패"),WAIT("대기");

    private String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OrderStatus getCode(String name){
        for(OrderStatus code : OrderStatus.values()) {
            if (code.getName().equals(name)) {
                return code;
            }
        }
        return null;
    }
}
