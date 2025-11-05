package com.baas.securities.enums;

public enum TransactionType {
    //send 삭제 송금 개념은 각자 계좌 입장에서 입금이고 출금인거니까
    BUY("매수"),SELL("매도"),DEPOSIT("입금"),WITHDRAW("출금");

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
