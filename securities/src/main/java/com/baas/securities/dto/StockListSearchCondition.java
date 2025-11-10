package com.baas.securities.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StockListSearchCondition {

    /**
     * 시가총액 : capitalization
     * 거래량 : volume
     */
    private String category;
    /**
     * 분류 구분 코드
     * 0(전체) 1(보통주) 2(우선주)
     */
    private Integer div;
    /**
     * 소속 구분 코드
     * 0 : 평균거래량
     * 1:거래증가율
     * 2:평균거래회전율
     * 3:거래금액순
     * 4:평균거래금액회전율
     */
    private Integer blng;

    /**
     * 0000:전체,
     * 0001:거래소,
     * 1001:코스닥,
     * 2001:코스피200
     */
    private String iscd;

    public StockListSearchCondition() {
        div = 0;
        blng = 0;
        iscd = "0000";
        category = "volume";
    }
}
