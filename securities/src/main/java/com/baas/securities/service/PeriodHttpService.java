package com.baas.securities.service;

import com.baas.securities.dto.stock.PeriodStockPriceDTO;
import com.baas.securities.dto.stock.PeriodStockRequestDTO;
import com.baas.securities.dto.stock.RealtimeStockDTO;
import com.baas.securities.enums.DetailUri;
import com.baas.securities.enums.Period;
import com.baas.securities.enums.TrId;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.NotFoundException;
import com.baas.securities.repository.PeriodStockPriceRepository;
import com.baas.securities.repository.entity.PeriodStockPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PeriodHttpService {
    @Value("${KIS_HTTP_DOMAIN}")
    private String baseUri;

    private final RestClient restClient;
    private final PeriodStockPriceRepository repository;

    public void saveDay(String ticker) {
        String query = "?FID_COND_MRKT_DIV_CODE=J&FID_PERIOD_DIV_CODE=D&FID_ORG_ADJ_PRC=0&FID_INPUT_ISCD=" + ticker;
        String end = LocalDate.now().toString().replaceAll("-", "");
        String start = LocalDate.now().minusDays(30).toString().replaceAll("-", "");
        query += "&FID_INPUT_DATE_1=" + start + "&FID_INPUT_DATE_2=" + end;

        String destUrl = baseUri + DetailUri.KOR_INDEX.getName() + query;

        save(ticker, destUrl, Period.DAY);
    }

    public void saveWeek(String ticker) {
        String query = "?FID_COND_MRKT_DIV_CODE=J&FID_PERIOD_DIV_CODE=W&FID_ORG_ADJ_PRC=0&FID_INPUT_ISCD=" + ticker;
        String end = LocalDate.now().toString().replaceAll("-", "");
        String start = LocalDate.now().minusWeeks(30).toString().replaceAll("-", "");
        query += "&FID_INPUT_DATE_1=" + start + "&FID_INPUT_DATE_2=" + end;

        String destUrl = baseUri + DetailUri.KOR_INDEX.getName() + query;

        save(ticker, destUrl, Period.WEEK);
    }

    public void saveMonth(String ticker) {
        String query = "?FID_COND_MRKT_DIV_CODE=J&FID_PERIOD_DIV_CODE=M&FID_ORG_ADJ_PRC=0&FID_INPUT_ISCD=" + ticker;
        String end = LocalDate.now().toString().replaceAll("-", "");
        String start = LocalDate.now().minusMonths(30).toString().replaceAll("-", "");
        query += "&FID_INPUT_DATE_1=" + start + "&FID_INPUT_DATE_2=" + end;

        String destUrl = baseUri + DetailUri.KOR_INDEX.getName() + query;

        save(ticker, destUrl, Period.MONTH);
    }

    public void saveYear(String ticker) {
        String query = "?FID_COND_MRKT_DIV_CODE=J&FID_PERIOD_DIV_CODE=Y&FID_ORG_ADJ_PRC=0&FID_INPUT_ISCD=" + ticker;
        String end = LocalDate.now().toString().replaceAll("-", "");
        String start = LocalDate.now().minusYears(30).toString().replaceAll("-", "");
        query += "&FID_INPUT_DATE_1=" + start + "&FID_INPUT_DATE_2=" + end;

        String destUrl = baseUri + DetailUri.KOR_INDEX.getName() + query;

        save(ticker, destUrl, Period.YEAR);
    }

    public PeriodStockPriceDTO getPeriodPrice(PeriodStockRequestDTO dto) {
        Period period = dto.getPeriod();
        String ticker = dto.getTicker();

        PeriodStockPrice entity = repository.findFirstByTickerAndPeriodOrderByDateTimeDesc(ticker, period)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TICKER_NOT_FOUND));

        return PeriodStockPriceDTO.builder()
                .ticker(entity.getTicker()).period(entity.getPeriod()).datas(entity.getDatas())
                .build();
    }

    private void save(String ticker, String destUrl, Period period) {
        ResponseEntity<Map> response = restClient.get()
                .uri(destUrl)
                .headers(httpHeaders -> {
                    httpHeaders.set("tr_id", TrId.KOR_PERIOD_STOCK_PRICE.getName());
                })
                .retrieve()
                .toEntity(Map.class);

        List<Map<String, String>> output = extractOutput(response, "output2");
        List<RealtimeStockDTO> resData = output.stream().map(data -> generateDTO(data, ticker)).toList();

        PeriodStockPrice entity = generateEntity(ticker, period, resData);
        repository.save(entity);
    }

    private List<Map<String, String>> extractOutput(ResponseEntity<Map> response, String key) {
        return (List<Map<String, String>>) response.getBody().get(key);
    }


    private RealtimeStockDTO generateDTO(Map<String, String> data, String ticker) {
        // 1. 데이터 파싱
        Double currentPrice = Double.parseDouble(data.get("stck_clpr")); // 종가
        BigInteger changeAmount = new BigInteger(data.get("prdy_vrss")); // 전일 대비
        BigInteger accumulatedVolume = new BigInteger(data.get("acml_vol")); // 누적 거래량

        // 2. 등락률 계산 (공식: (전일대비 / (현재가 - 전일대비)) * 100)
        // 전일 종가 = 현재가 - 전일대비
        BigDecimal bdPrice = BigDecimal.valueOf(currentPrice);
        BigDecimal bdChange = new BigDecimal(data.get("prdy_vrss"));
        BigDecimal prevClose = bdPrice.subtract(bdChange);

        BigDecimal calculatedRate = BigDecimal.ZERO;
        if (prevClose.compareTo(BigDecimal.ZERO) != 0) {
            // 소수점 2자리까지 반올림 (예: 5.23)
            calculatedRate = bdChange.divide(prevClose, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 3. 빌더 반환
        return RealtimeStockDTO.builder()
                .ticker(ticker)
                .tradeTime(data.get("stck_bsop_date"))
                .price(currentPrice)
                .change(changeAmount)
                .changeRate(calculatedRate)
                .tradeVolume(accumulatedVolume)
                .accTradeVolume(accumulatedVolume)
                .accTradeValue(new BigInteger(data.get("acml_tr_pbmn")))
                .openPrice(Double.parseDouble(data.get("stck_oprc")))
                .highPrice(Double.parseDouble(data.get("stck_hgpr")))
                .lowPrice(Double.parseDouble(data.get("stck_lwpr")))
                .build();
    }

    private PeriodStockPrice generateEntity(String ticker, Period period, List<RealtimeStockDTO> datas) {
        return PeriodStockPrice.builder()
                .ticker(ticker)
                .period(period)
                .dateTime(LocalDateTime.now())
                .datas(datas)
                .build();
    }
}
