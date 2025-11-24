package com.baas.securities.controller;

import com.baas.securities.dto.ResponseDTO;
import com.baas.securities.dto.transaction.TransactionsReqDTO;
import com.baas.securities.repository.entity.TransactionOrder;
import com.baas.securities.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseDTO transactions(@ModelAttribute TransactionsReqDTO dto) {
        log.info("accepted request={}", dto);
        List<TransactionOrder> transactionOrders = null;
        if (dto.getIsOrder()) {
            transactionOrders = transactionService.transactionOrders(dto);
        } else {

        }

        return new ResponseDTO(HttpStatus.OK, "거래 목록이 조회되었습니다.", transactionOrders);
    }
}
