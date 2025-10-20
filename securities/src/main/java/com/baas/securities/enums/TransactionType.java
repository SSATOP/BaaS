package com.baas.securities.enums;

public enum TransactionType {
    BUY("매수"),SELL("매도"),SEND("송금"),DEPOSIT("입금");

    private String name;

    TransactionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TransactionType getCode(String name){
        for(TransactionType code : TransactionType.values()){
            if(code.getName().equals(name)){
                return code;
            }
        }

        return null;
    }
}
