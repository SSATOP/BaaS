package com.baas.securities.service;

import com.baas.securities.dto.StockInfoDTO;
import com.baas.securities.dto.StockListDTO;
import com.baas.securities.dto.StockListSearchCondition;
import com.baas.securities.enums.DetailUri;
import com.baas.securities.enums.TrId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class StockHttpService {

    @Value("${KIS_HTTP_DOMAIN}")
    private String baseUri;

    private final RestClient restClient;

    public StockListDTO getKorStockVolumeList(StockListSearchCondition condition) {
        String queryString = "?FID_COND_MRKT_DIV_CODE=NX&FID_COND_SCR_DIV_CODE=20171&FID_INPUT_ISCD=0000&FID_DIV_CLS_CODE=%d&FID_BLNG_CLS_CODE=%d".formatted(condition.getDiv(), condition.getBlng()) +
                "&FID_TRGT_CLS_CODE=&FID_TRGT_EXLS_CLS_CODE=&FID_INPUT_PRICE_1=&FID_INPUT_PRICE_2=&FID_VOL_CNT=&FID_INPUT_DATE_1=";

        String destUrl = baseUri + DetailUri.KOR_STOCK_VOLUME_RANK.getName() + queryString;
        log.info("destUrl={}", destUrl);

        ResponseEntity<Map> response = callKisApi(destUrl, TrId.KOR_STOCK_VOLUME_RANK.getName());

        List<Object> output = extractOutput(response, "output");
        List<StockInfoDTO> stockList = parseToStockDTOList(output);

        return generateStockListDTO(stockList, condition.getCategory());
    }

    public StockListDTO getKorStockCapitalizationList(StockListSearchCondition condition) {
        String queryString = "?fid_input_price_2&fid_cond_mrkt_div_code=NX" +
                "&fid_cond_scr_div_code=20174&fid_div_cls_code=%d&fid_input_iscd=%s".formatted(condition.getDiv(), condition.getIscd()) +
                "&fid_trgt_cls_code=&fid_trgt_exls_cls_code=&fid_input_price_1&fid_vol_cnt";

        String destUrl = baseUri + DetailUri.KOR_STOCK_MARKET_CAPITALIZATION_RANK.getName() + queryString;

        ResponseEntity<Map> response = callKisApi(destUrl, TrId.KOR_STOCK_MARKET_CAPITALIZATION_RANK.getName());

        List<Object> output = extractOutput(response, "output");
        List<StockInfoDTO> stockList = parseToStockDTOList(output);

        return generateStockListDTO(stockList, condition.getCategory());
    }

    private ResponseEntity<Map> callKisApi(String destUrl, String trId) {
        return restClient.get()
                .uri(destUrl)
                .headers(httpHeaders -> {
                    httpHeaders.set("tr_id", trId);
                })
                .retrieve()
                .toEntity(Map.class);
    }

    private StockListDTO generateStockListDTO(List<StockInfoDTO> list, String category) {
        return StockListDTO.builder()
                .stocks(list)
                .category(category)
                .build();
    }

    private List<Object> extractOutput(ResponseEntity<Map> response, String key) {
        return (List<Object>) response.getBody().get(key);
    }

    private List<StockInfoDTO> parseToStockDTOList(List<Object> list) {
        return list.stream().map(item -> generateStockInfoDTO((Map<String, String>) item))
                .toList();
    }

    private StockInfoDTO generateStockInfoDTO(Map<String, String> output) {
        return StockInfoDTO.builder()
                .ticker(output.get("mksc_shrn_iscd"))
                .stockName(output.get("hts_kor_isnm"))
                .currentPrice(Double.parseDouble(output.get("stck_prpr")))
                .change(Double.parseDouble(output.get("prdy_vrss")))
                .dataRank(Long.parseLong(output.get("data_rank")))
                .changeRate(Double.parseDouble(output.get("prdy_ctrt")))
                .changeSign(output.get("prdy_vrss_sign"))
                .build();
    }
}
