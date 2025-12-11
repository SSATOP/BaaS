package com.baas.securities.service;

import com.baas.securities.dto.transaction.TransactionsReqDTO;
import com.baas.securities.repository.entity.Transaction;
import com.baas.securities.repository.entity.TransactionOrder;
import com.baas.securities.repository.entity.TransactionRelative;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TransactionServiceTest {

    @Autowired
    TransactionService service;

    @Test
    @DisplayName("call list service")
    void callList() {
        // given
        TransactionsReqDTO dto = new TransactionsReqDTO();
//        dto.setStartDate(LocalDateTime.now().minusDays(20));
//        dto.setEndDate(LocalDateTime.now());
        dto.setAccountId("1debbcd8-fad7-47e3-a18c-b8dd845d873c");
        List<TransactionOrder> transactionOrders = service.transactionOrders(dto);
        transactionOrders.forEach(System.out::println);
        // when

        // then
    }

    @Test
    @DisplayName("relative transactions")
    void callRelative() {
        // given
        TransactionsReqDTO dto = new TransactionsReqDTO();
        dto.setAccountId("1debbcd8-fad7-47e3-a18c-b8dd845d873c");
        // when
        List<TransactionRelative> transactionRelatives = service.transactionRelatives(dto);
        transactionRelatives.forEach(System.out::println);
        // then
    }

}