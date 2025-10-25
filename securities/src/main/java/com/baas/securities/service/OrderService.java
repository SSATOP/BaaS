package com.baas.securities.service;

import com.baas.securities.dto.stock.OrderResDTO;
import com.baas.securities.dto.stock.StockOrderDTO;
import com.baas.securities.enums.OrderStatus;
import com.baas.securities.enums.TransactionStatus;
import com.baas.securities.enums.TransactionType;
import com.baas.securities.repository.*;
import com.baas.securities.repository.entity.Account;
import com.baas.securities.repository.entity.Holdings;
import com.baas.securities.repository.entity.Order;
import com.baas.securities.repository.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final HoldingsRepository holdingsRepository;
    private final UserRepository userRepository;


    // Todo : 유저 확인 메서드 필요
    // Todo : 주문에서 종목 코드 별로 관리를 하여서 보유종목에 추가 하는 절차 필요
    public OrderResDTO buyOrder(StockOrderDTO dto){
        // step1. 계좌 확인
        String accountId  =dto.getAccountId();
        // 빈 객체일시 예외를 던짐
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("잘못된 토큰입니다."));


        // step2-1. 주문 가능 확인
        // step2-2. 주문 오류-> 거래 생성 불가
        if(invalidateBalance(account,dto)){
            throw new IllegalArgumentException("잔액 부족");
        }
        // step3. 주문 생성
        Order order = generateOrder(dto);
        orderRepository.save(order);

        // step4. 거래 생성
        Transaction transaction = generateTransaction(account, order,TransactionType.BUY);
        order.updateOrderStatus(OrderStatus.SUCCESS,null);
        transactionRepository.save(transaction);
        // step5. 계좌금액 차감
        account.updateBalance(account.getBalance()-(long)transaction.getAmount().doubleValue());

        // step6: 보유종목 생성 ?? 생성으로 해야할지 추가로 해야할지
        Holdings holdings = generateHoldings(dto);
        holdingsRepository.save(holdings);
        //step 6 : 보유종목 추가(dto에서 주문량 가져올지 order에서 가져올지 묻기)
        holdings.updateQuantity(holdings.getQuantity() + dto.getQuantity());


        return generateOrderResDTO(order.getId(),OrderStatus.SUCCESS);
    }

    // Todo : 보유 종목에 각 회사별 주식별(종목코드별)로 총액도 관리해야하는지 논의
    public OrderResDTO sellOrder(StockOrderDTO dto){

        // step1. 계좌 확인
        String accountId  =dto.getAccountId();
        // 빈 객체일시 예외를 던짐
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("잘못된 토큰입니다."));

        // step3: 보유종목 생성 -> 생성이 아니라 찾는걸로
        Holdings holdings = holdingsRepository.findByUserIdAndTicker(dto.getAccountId(), dto.getTicker())
                .orElseThrow(() -> new IllegalArgumentException("보유하지 않은 종목입니다."));

        // step4-1.주문 수량 확인
        // step4-2.주문 오류 -> 거래 생성 불가
        if(invalidateQuantity(dto,holdings)){
            throw new IllegalArgumentException("보유 수량 부족");
        }

        // step2. 주문 생성
        Order order = generateOrder(dto);
        orderRepository.save(order);

        // step4. 거래 생성
        Transaction transaction = generateTransaction(account, order,TransactionType.SELL);
        order.updateOrderStatus(OrderStatus.SUCCESS,null);
        transactionRepository.save(transaction);
        //step5. 계좌 금액 증가
        account.updateBalance(account.getBalance()+(long)transaction.getAmount().doubleValue());

        //step 7 : 보유종목 감소(dto에서 주문량 가져올지 order에서 가져올지 묻기)

        holdings.updateQuantity(holdings.getQuantity()-dto.getQuantity());
        // step 7-1 : 보유 주식 다 팔았을 경우 해당 보유 종목 삭제
        if(holdings.getQuantity() == 0){
            holdingsRepository.deleteById(holdings.getId());
        }

        return generateOrderResDTO(order.getId(),OrderStatus.SUCCESS);
    }


    private Holdings generateHoldings(StockOrderDTO dto){
        return Holdings.builder()
                .symbol(dto.getTicker())
                //  .userId() 필요한가?
                .quantity(dto.getQuantity())
               // .avgPrice()
                //.profitLoss()
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    private Order generateOrder(StockOrderDTO dto){
        return Order.builder()
                .accountId(dto.getAccountId())
                .price(dto.getPrice())
                .orderType(dto.getOrderType())
                .orderStatus(OrderStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .symbol(dto.getTicker())
                .quantity(dto.getQuantity())
                .totalAmount(calAmount(dto))
                .build();

    }

    private Double calAmount(StockOrderDTO dto){
        return dto.getQuantity() * dto.getPrice();
    }

    private boolean invalidateBalance(Account account,StockOrderDTO dto){
        return account.getBalance() < dto.getQuantity() * dto.getPrice();

    }

    // todo : 종목코드별로 관리 추가 필요
    private boolean invalidateQuantity(StockOrderDTO dto, Holdings holdings){
        return dto.getQuantity() > holdings.getQuantity();
    }

    private Transaction generateTransaction(Account account, Order order, TransactionType transactionType){
        return Transaction.builder()
                .accountId(account.getId())
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .transactionType(transactionType)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .build();

    }

    private OrderResDTO generateOrderResDTO(String orderId, OrderStatus orderStatus){
        return OrderResDTO.builder()
                .orderId(orderId)
                .status(orderStatus)
                .build();
    }
}
