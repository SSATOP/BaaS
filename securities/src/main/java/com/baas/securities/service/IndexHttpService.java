package com.baas.securities.service;

import com.baas.securities.enums.DetailUri;
import com.baas.securities.dto.IndexResDTO;
import com.baas.securities.enums.KorIndex;
import com.baas.securities.enums.TrId;
import com.baas.securities.enums.USAIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * 국내 - 해외 지수 요청
 */
public class IndexHttpService {

    @Value("${KIS_HTTP_DOMAIN}")
    private String baseUri;

    private final RestClient restClient;

    public List<Object> getStockKorIndexList() {
        String queryString = "?FID_COND_MRKT_DIV_CODE=U&FID_INPUT_ISCD=";

        List<Object> res = new ArrayList<>();

        for (KorIndex inx : KorIndex.values()) {
            String destUrl = baseUri + DetailUri.KOR_INDEX.getName() + queryString + inx.getCode();

            ResponseEntity<Map> response = restClient.get()
                    .uri(destUrl)
                    .headers(httpHeaders -> {
                        httpHeaders.set("tr_id", TrId.KOR_INDEX.getName());
                    })
                    .retrieve()
                    .toEntity(Map.class);

            Map<String, String> output = extractOutput(response, "output");

            IndexResDTO dto = generateKorIndexResDTO(output, inx.getIndexName());

            res.add(dto);
        }
        return res;
    }

    public List<Object> getStockUSAIndexList() {
        String queryString = "?FID_COND_MRKT_DIV_CODE=N&FID_INPUT_ISCD=";
        String afterQueryString = "&FID_INPUT_DATE_1&FID_INPUT_DATE_2&FID_PERIOD_DIV_CODE";

        List<Object> res = new ArrayList<>();

        for (USAIndex inx : USAIndex.values()) {
            String destUrl = baseUri + DetailUri.USA_INDEX.getName() + queryString + inx.getCode() + afterQueryString;

            ResponseEntity<Map> response = restClient.get()
                    .uri(destUrl)
                    .headers(httpHeaders -> {
                        httpHeaders.set("tr_id", TrId.USA_INDEX.getName());
                    })
                    .retrieve()
                    .toEntity(Map.class);

            log.info("destUrl={}, response={}", destUrl, response.getBody().keySet());

            Map<String, String> output = extractOutput(response, "output1");

            IndexResDTO dto = generateUSAIndexResDTO(output, inx.getIndexName());

            res.add(dto);
        }
        return res;
    }


    private Map<String, String> extractOutput(ResponseEntity<Map> response, String key) {
        return (Map<String, String>) response.getBody().get(key);
    }

    private IndexResDTO generateKorIndexResDTO(Map<String, String> output, String indexName) {
        return IndexResDTO.builder()
                .indexName(indexName)
                .value(Double.parseDouble(output.get("bstp_nmix_prpr")))
                .change(Double.parseDouble(output.get("bstp_nmix_prdy_vrss")))
                .changeRate(Double.parseDouble(output.get("bstp_nmix_prdy_ctrt")))
                .build();
    }

    private IndexResDTO generateUSAIndexResDTO(Map<String, String> output, String indexName) {
        return IndexResDTO.builder()
                .indexName(indexName)
                .value(Double.parseDouble(output.get("ovrs_nmix_prpr")))
                .change(Double.parseDouble(output.get("ovrs_nmix_prdy_vrss")))
                .changeRate(Double.parseDouble(output.get("prdy_ctrt")))
                .build();
    }
}
