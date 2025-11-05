package com.baas.securities.enums;

public enum TransactionStatus {
    SUCCESS("성공"),FAIL("실패");
    private String name;

    TransactionStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TransactionStatus getCode(String name){
        for(TransactionStatus code : TransactionStatus.values()) {
            if (code.getName().equals(name)) {
                return code;
            }
        }
        return null;
    }
}
