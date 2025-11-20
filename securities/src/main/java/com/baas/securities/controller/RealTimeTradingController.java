package com.baas.securities.controller;

import com.baas.securities.dto.AccIdUserIdInfoDTO;
import com.baas.securities.dto.security.AuthUser;
import com.baas.securities.dto.stock.OrderResDTO;
import com.baas.securities.dto.ResponseDTO;
import com.baas.securities.dto.stock.StockOrderDTO;
import com.baas.securities.exception.ErrorCode;
import com.baas.securities.exception.ex.BadRequestException;
import com.baas.securities.exception.ex.HttpBaseException;
import com.baas.securities.security.resolver.Login;
import com.baas.securities.service.AccountService;
import com.baas.securities.service.KisService;
import com.baas.securities.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RealTimeTradingController {
    private final OrderService orderService;
    private final KisService kisService;

    private final AccountService accountService;

    /**
     * SEC_10_11 : 매수 매도
     */
    @MessageMapping("/stock/order")
    @SendToUser("/sub/stock/order")
    public ResponseDTO order(@RequestBody StockOrderDTO dto, StompHeaderAccessor accessor, @Login AuthUser user) {
        String sessionId = accessor.getSessionId();
        String email = user.getEmail();

        log.info("user = {}, sessionId={}", email, sessionId);

        // --- [수정 포인트] try-catch를 벗겨내고 로직만 일자로 배치 ---

        // 1. 유효성 검증
        // 여기서 예외가 터지면 -> 알아서 메서드 실행이 중단되고 ControllerAdvice로 날아갑니다.
        AccIdUserIdInfoDTO infoDto = accountService.validateAccountByEmailAndAccountNumber(email, dto.getAccountNumber());

        OrderResDTO orderResDTO = null;

        // 2. 주문 처리
        switch (dto.getOrderType()) {
            case "BUY" -> orderResDTO = orderService.buyOrder(dto, infoDto);
            case "SELL" -> orderResDTO = orderService.sellOrder(dto, infoDto);
            default ->
                // 여기서 throw를 하면 -> 역시 ControllerAdvice가 잡아서 처리합니다.
                    throw new BadRequestException(ErrorCode.INVALID_ORDER);
        }

        // 3. 성공 응답
        return generateResponseDto(HttpStatus.CREATED, "접수 완료", orderResDTO);
    }

    public ResponseDTO generateResponseDto(HttpStatus status, String message, OrderResDTO orderResDTO){
        return ResponseDTO.builder()
                .status(status)
                .message(message)
                .data(orderResDTO)
                .build();
    }

    /**
     * SEC-9 실시간 시세
     * 구독 컨트롤러
     * 구독 해지 로직은 ChanelInterceptor 에서 진행.
     */
    // stomppreHandler가 이 역할 대신하며, race condition 유발할까봐 주석 처리
//    @MessageMapping("/stock/{ticker}")
//    public void subRealtimeStockInfo(StompHeaderAccessor accessor, @DestinationVariable String ticker) throws IOException {
//        log.info("sub stomp: session id={}, ticker={}", accessor.getSessionId(), ticker);
//        String sessionId = accessor.getSessionId();
//        kisService.subRealtimeStock(sessionId, ticker);
//    }
}
