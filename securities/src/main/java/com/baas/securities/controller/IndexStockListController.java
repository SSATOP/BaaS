package com.baas.securities.controller;

import com.baas.securities.dto.ResponseDTO;
import com.baas.securities.dto.StockListDTO;
import com.baas.securities.dto.StockListSearchCondition;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.InternalServerErrorException;
import com.baas.securities.service.IndexHttpService;
import com.baas.securities.service.StockHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/market")
public class IndexStockListController {

    private final IndexHttpService indexHttpService;
    private final StockHttpService stockHttpService;

    @GetMapping("/indices")
    public ResponseDTO korIndex(@RequestParam String country) {
        log.info("TestController.korIndex");

        List<Object> data = null;

        switch (country) {
            case "kor" -> data = indexHttpService.getStockKorIndexList();
            case "usa" -> data = indexHttpService.getStockUSAIndexList();
        }

        if (data == null) {
            throw new InternalServerErrorException(ErrorCode.MARKET_DATA_ERROR);
        }

        return createResDTO(HttpStatus.OK, "조회 성공", data);
    }

    @GetMapping("/list")
    public ResponseDTO stockList(@ModelAttribute StockListSearchCondition condition) {
        log.info("condition={}", condition.toString());

        StockListDTO dto = null;

        switch(condition.getCategory()) {
            case "volume" -> dto = stockHttpService.getKorStockVolumeList(condition);
            case "capitalization" -> dto = stockHttpService.getKorStockCapitalizationList(condition);
        }

        return createResDTO(HttpStatus.OK, "조회 성공", dto);
    }

    private ResponseDTO createResDTO(HttpStatus status, String msg, Object data) {
        return ResponseDTO.builder()
                .status(status)
                .message(msg)
                .data(data)
                .build();
    }
}
