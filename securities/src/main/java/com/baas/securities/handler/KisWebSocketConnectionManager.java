package com.baas.securities.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.net.URI;

@Slf4j
public class KisWebSocketConnectionManager extends WebSocketConnectionManager {

    public KisWebSocketConnectionManager(WebSocketClient client, WebSocketHandler webSocketHandler, URI uri) {
        super(client, webSocketHandler, uri);
    }

    @Override
    protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
        return new ReconnectWebSocketHandler(handler, this::openConnection);
    }

    private static class ReconnectWebSocketHandler extends WebSocketHandlerDecorator {

        private final Runnable closeTask;

        public ReconnectWebSocketHandler(WebSocketHandler delegate, Runnable closeTask) {
            super(delegate);
            this.closeTask = closeTask;
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            super.afterConnectionClosed(session, closeStatus);
            this.closeTask.run();
            log.info("reconnect websocket by ReconnectWebSocketHandler");
        }
    }
}
