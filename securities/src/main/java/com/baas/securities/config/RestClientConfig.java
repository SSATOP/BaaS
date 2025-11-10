package com.baas.securities.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Configuration
@Slf4j
public class RestClientConfig {

    @Value("${APP_KEY}")
    private String appKey;
    @Value("${APP_SECRET}")
    private String appSecret;
    @Value("${HTTP_ACCESS_TOKEN}")
    private String accessToken;

    private static final int CONNECTION_TIMEOUT_SECONDS = 1;
    private static final int READ_TIMEOUT_SECONDS = 5;

    @Bean
    public RestClient javaRestClient(RestClient.Builder restClientBuilder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));

        return restClientBuilder
                .requestFactory(requestFactory)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.set("Authorization", "Bearer " + accessToken);
                    httpHeaders.set("appkey", appKey);
                    httpHeaders.set("appsecret", appSecret);
                    httpHeaders.set("custtype", "P");
                })
                .defaultStatusHandler(
                        statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(),
                        (request, response) -> {
                            log.error("HTTP request failed.");
                            log.error("Request: {} {}", request.getMethod(), request.getURI());
                            log.error("Response: {} {}", response.getStatusCode(), response.getStatusText());

                            if (response.getStatusCode().is4xxClientError()) {
                                throw new RuntimeException("Client exception");
                            }
                            if (response.getStatusCode().is5xxServerError()) {
                                throw new RuntimeException("Server exception");
                            }
                            throw new RestClientException("Unexpected response status: " + response.getStatusCode());
                        }
                )
                .build();
    }
}
