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
        // Todo : 요청한 사용자와 계좌가 일치하는지 절차 필요
        String email = user.getEmail();
        log.info("user = {}, sessionId={}", email, sessionId);
        try {
            // --- [수정] 서비스 로직 전체를 try-catch로 묶음 ---
            AccIdUserIdInfoDTO infoDto = accountService.validateAccountByEmailAndAccountNumber(email, dto.getAccountNumber());

            OrderResDTO orderResDTO = null;

            switch (dto.getOrderType()) {
                case "BUY" -> orderResDTO = orderService.buyOrder(dto, infoDto);
                case "SELL" -> orderResDTO = orderService.sellOrder(dto, infoDto);
                default ->
                    // [추가] 정의되지 않은 주문 타입에 대한 방어
                        throw new BadRequestException(ErrorCode.INVALID_ORDER);
            }

            // 성공 응답
            return generateResponseDto(HttpStatus.CREATED, "접수 완료", orderResDTO);

        }
        // [수정] 우리가 정의한 커스텀 예외들을 catch
        catch (HttpBaseException e) {
            log.warn("주문 처리 실패 [{}]: {}", e.getCode(), e.getMessage());
            // ErrorCode의 HttpStatus를 사용 (예: 400, 404)
            HttpStatus status = ErrorCode.valueOf(e.getCode()).getStatus();
            // 실패 응답 DTO를 생성하여 클라이언트에게 전송
            return ResponseDTO.builder()
                    .status(status)
                    .message(e.getMessage())
                    .data(e.getCode()) // (선택) 데이터 대신 에러 코드를 전송
                    .build();
        }
        // [수정] 기타 예상치 못한 500 에러
        catch (Exception e) {
            log.error("WebSocket 주문 처리 중 심각한 오류 발생", e);
            return ResponseDTO.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버 내부 오류가 발생했습니다.")
                    .data("SERVER-5000")
                    .build();
        }
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
