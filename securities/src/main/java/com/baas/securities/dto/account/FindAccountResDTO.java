package com.baas.securities.dto.account;

import com.baas.securities.repository.entity.Account;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class FindAccountResDTO {
    private String accountId;
    private String accountNumber;
    private BigDecimal balance;        // 계좌 잔고
    private BigDecimal totalBalance;
    private String accountType;        // 계좌 타입
    private List<HoldingDTO> holdings;

    @Data
    @NoArgsConstructor
    public static class HoldingDTO {
        private String ticker;
        private String name;
        private double avgPrice;
        private double nowPrice;
        private double rate;
        private Long quantity;
        private double totalPrice;

        @Builder
        public HoldingDTO(String ticker, String name, double avgPrice, double nowPrice, Long quantity) {
            this.ticker = ticker;
            this.name = name;
            this.avgPrice = avgPrice;
            this.nowPrice = nowPrice;
            this.rate = avgPrice / nowPrice;
            this.totalPrice = avgPrice * quantity;
            this.quantity = quantity;
        }
    }

    public static FindAccountResDTO generate(Account account, List<HoldingDTO> holdings) {
        BigDecimal totalBalance = account.getBalance();
        double holdingsSum = holdings.stream().mapToDouble(holding -> holding.totalPrice).sum();
        totalBalance = totalBalance.add(new BigDecimal(holdingsSum));

        return FindAccountResDTO.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .totalBalance(totalBalance)
                .accountType(account.getAccountType())
                .holdings(holdings)
                .build();
    }
}
