package com.baas.securities.config;

import com.baas.securities.handler.KisWebSocketConnectionManager;
import com.baas.securities.handler.KisWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${SOCKET_URL}")
    private String url;
    private final KisWebSocketHandler kisHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        WebSocketClient client = new StandardWebSocketClient();
        try {
            KisWebSocketConnectionManager manager = new KisWebSocketConnectionManager(client, kisHandler, new URI(url));
            manager.start();
        } catch (URISyntaxException e) {
            throw new RuntimeException("유효하지 않은 웹소켓 주소입니다.");
        }
    }
}
