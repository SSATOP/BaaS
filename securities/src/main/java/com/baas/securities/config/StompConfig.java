package com.baas.securities.config;

import com.baas.securities.security.resolver.StompLoginAnnotationResolver;
import com.baas.securities.ws.AuthStompHandler;
import com.baas.securities.ws.DontNeedAuthInterceptor;
import com.baas.securities.ws.StompPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompPreHandler stompPreHandler;
    private final DontNeedAuthInterceptor dontNeedAuthInterceptor;
    private final AuthStompHandler authStompHandler;

    private final StompLoginAnnotationResolver stompLoginAnnotationResolver;

    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 구독(sub) : 접두사로 시작하는 메시지를 브로커가 처리하도록 설정합니다. 클라이언트는 이 접두사로 시작하는 주제를 구독하여 메시지를 받을 수 있습니다.
        // 예를 들어, 소켓 통신에서 사용자가 특정 메시지를 받기위해 "/sub"이라는 prefix 기반 메시지 수신을 위해 Subscribe합니다.
        config.enableSimpleBroker("/sub");

        // 발행(pub) : 접두사로 시작하는 메시지는 @MessageMapping이 달린 메서드로 라우팅됩니다. 클라이언트가 서버로 메시지를 보낼 때 이 접두사를 사용합니다.
        // 예를 들어, 소켓 통신에서 사용자가 특정 메시지를 전송하기 위해 "/pub"라는 prefix 기반 메시지 전송을 위해 Publish 합니다.
        config.setApplicationDestinationPrefixes("/pub");

        config.setUserDestinationPrefix("/user");
    }

    /**
     * ws-stomp로 연결할때는 jwt 필요없음.
     * ws-stomp-stock-order 로 연결할떄는 jwt 필요.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //STOMP(WebSocket 메시지 브로커 프로토콜) 엔드포인트를 등록하는 메서드로, 클라이언트가 WebSocket에 연결할 수 있는 엔드포인트를 정의합니다.
        //addEndpoint() : 클라이언트가 WebSocket에 연결하기 위한 엔드포인트를 "/ws-stomp"로 설정합니다.
        registry.addEndpoint("/ws-stomp")
                //클라이언트의 origin을 명시적으로 지정합니다.
                .setAllowedOrigins("http://localhost:5500", "http://127.0.0.1:5500")
                .addInterceptors(dontNeedAuthInterceptor)
                .withSockJS();
        //WebSocket을 지원하지 않는 브라우저에서도 SockJS를 통해 WebSocket 기능을 사용할 수 있게 합니다.
        registry.addEndpoint("/ws-stomp-stock-order")
                //클라이언트의 origin을 명시적으로 지정합니다.
                .setAllowedOrigins("http://localhost:5500", "http://127.0.0.1:5500")
                .withSockJS();
    }

    /**
     * stomp interceptor 추가.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authStompHandler, stompPreHandler);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(1_048_576);
        registration.setSendBufferSizeLimit(1_048_576);
        registration.setSendTimeLimit(30_000);
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(stompLoginAnnotationResolver);
    }
}
