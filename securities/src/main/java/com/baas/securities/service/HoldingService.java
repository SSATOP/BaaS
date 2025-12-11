package com.baas.securities.service;

import com.baas.securities.dto.account.FindAccountResDTO;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.NotFoundException;
import com.baas.securities.repository.HoldingsRepository;
import com.baas.securities.repository.RealtimeStockPriceRepository;
import com.baas.securities.repository.SymbolRepository;
import com.baas.securities.repository.entity.Holdings;
import com.baas.securities.repository.entity.RealTimePrice;
import com.baas.securities.repository.entity.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HoldingService {

    private final HoldingsRepository holdingsRepository;
    private final RealtimeStockPriceRepository realtimeStockPriceRepository;
    private final SymbolRepository symbolRepository;

    /**
     * holding에 종목의 최신 가격을 반환해야함.
     */
    public List<FindAccountResDTO.HoldingDTO> findAllByAccountId(String id) {
        return holdingsRepository.findAllByAccountId(id).stream()
                .map(holdings -> generate(holdings))
                .toList();
    }

    private FindAccountResDTO.HoldingDTO generate(Holdings holdings) {
        Symbol stockSymbol = symbolRepository.findBySymbol(holdings.getSymbol())
                .orElseThrow(() -> new NotFoundException(ErrorCode.STOCK_NOT_FOUND_ORDER));

        RealTimePrice realTimePrice = realtimeStockPriceRepository
                .findTopByTickerOrderByTradeTimeDesc(holdings.getSymbol())
                .orElseThrow(() -> new NotFoundException(ErrorCode.TICKER_NOT_FOUND));

        return new FindAccountResDTO.HoldingDTO(holdings.getSymbol(), stockSymbol.getName(), holdings.getAvgPrice(), realTimePrice.getPrice(), holdings.getQuantity());
    }
}
