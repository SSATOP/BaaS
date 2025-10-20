package com.baas.securities.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketMessageMaker {

    private final ObjectMapper objectMapper;

    /**
     * 종목 구독 요청 메시지 to KIS
     */
    public String buildSubRequest(String ticker, String approvalKey) throws JsonProcessingException {
        return buildRequest(ticker, approvalKey, "1");
    }

    /**
     * 종목 구독 해지 요청 메시지 to KIS
     */
    public String buildUnsubRequest(String ticker, String approvalKey) throws JsonProcessingException {
        return buildRequest(ticker, approvalKey, "2");
    }


    private String buildRequest(String ticker, String approvalKey, String trType) throws JsonProcessingException {
        Map<String, String> header = new HashMap<>();

        header.put("approval_key", approvalKey);
        header.put("custtype", "P");
        header.put("tr_type", trType);
        header.put("content-type", "utf-8");

        Map<String, Object> body = new HashMap<>();
        Map<String, String> input = new  HashMap<>();

        input.put("tr_id", "H0UNCNT0");
        input.put("tr_key", ticker);

        body.put("input", input);

        Map<String, Object> request = new  HashMap<>();
        request.put("header", header);
        request.put("body", body);

        return objectMapper.writeValueAsString(request);
    }

    public String makePingPongMsg() throws JsonProcessingException {
        Map<String, Object> msg = new HashMap<>();

        Map<String, String> header = new HashMap<>();
        header.put("tr_id", "PINGPONG");

        msg.put("header", header);

        return objectMapper.writeValueAsString(msg);
    }

}
