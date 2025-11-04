package com.baas.securities.service;

import com.baas.securities.dto.AccIdUserIdInfoDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final HoldingsRepository holdingsRepository;
    private final SymbolRepository symbolRepository;
    private final UserRepository userRepository;


    // Todo : 유저 확인 메서드 필요
    // Todo : 주문에서 종목 코드 별로 관리를 하여서 보유종목에 추가 하는 절차 필요
    public OrderResDTO buyOrder(StockOrderDTO dto, AccIdUserIdInfoDTO infoDto) {
        // step1. 계좌 확인
        String accountNumber  = dto.getAccountNumber();
        // 빈 객체일시 예외를 던짐
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new IllegalArgumentException("잘못된 토큰입니다."));

        // step2-1. 주문 가능 확인 : 종목 존재 확인
        validateSymbol(dto.getTicker());
        // step2-2. 주문 오류-> 거래 생성 불가
        if(invalidateBalance(account,dto)){
            throw new IllegalArgumentException("잔액 부족");
        }
        // step3. 주문 생성
        Order order = generateOrder(account, dto, infoDto);
        orderRepository.save(order); // mybatis 땐 모든 수정사항 후에 save

        // step4. 거래 생성
        Transaction transaction = generateTransaction(account, order,TransactionType.BUY);
        order.updateOrderStatus(OrderStatus.SUCCESS,null);

//        orderRepository.save(order); // mybatis 땐 모든 수정사항 후에 save
        transactionRepository.save(transaction);
        // step5. 계좌금액 차감
        account.updateBalance(account.getBalance().subtract(transaction.getAmount()));

        // step6: 보유종목 생성 ?? 생성으로 해야할지 추가로 해야할지
        Holdings holdings = generateHoldings(dto);
//        holdingsRepository.save(holdings); // mybatis 땐 모든 수정사항 후에 save
        //step 6 : 보유종목 추가(dto에서 주문량 가져올지 order에서 가져올지 묻기)
        holdings.updateQuantity(holdings.getQuantity() + dto.getQuantity());
        holdingsRepository.save(holdings); // mybatis 땐 모든 수정사항 후에 save

        return generateOrderResDTO(order.getId(),OrderStatus.SUCCESS);
    }

    // Todo : 보유 종목에 각 회사별 주식별(종목코드별)로 총액도 관리해야하는지 논의
    public OrderResDTO sellOrder(StockOrderDTO dto, AccIdUserIdInfoDTO infoDto){

        // step1. 계좌 확인
        String accountNumber  = dto.getAccountNumber();
        // 빈 객체일시 예외를 던짐
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new IllegalArgumentException("잘못된 토큰입니다."));

        // step2 : 종목 존재 확인
        validateSymbol(dto.getTicker());

        // step3: 보유종목 생성 -> 생성이 아니라 찾는걸로
        Holdings holdings = holdingsRepository.findByUserIdAndTicker(account.getId(), dto.getTicker())
                .orElseThrow(() -> new IllegalArgumentException("보유하지 않은 종목입니다."));

        // step4-1.주문 수량 확인
        // step4-2.주문 오류 -> 거래 생성 불가
        if(invalidateQuantity(dto,holdings)){
            throw new IllegalArgumentException("보유 수량 부족");
        }

        // step5. 주문 생성
        Order order = generateOrder(account, dto, infoDto);
        orderRepository.save(order);

        // step6. 거래 생성
        Transaction transaction = generateTransaction(account, order,TransactionType.SELL);
        order.updateOrderStatus(OrderStatus.SUCCESS,null);
        transactionRepository.save(transaction);
        //step7. 계좌 금액 증가
        account.updateBalance(account.getBalance().add(transaction.getAmount()));

        //step 8 : 보유종목 감소(dto에서 주문량 가져올지 order에서 가져올지 묻기)

        holdings.updateQuantity(holdings.getQuantity()-dto.getQuantity());
        // step 8-1 : 보유 주식 다 팔았을 경우 해당 보유 종목 삭제
        if(holdings.getQuantity() == 0){
            holdingsRepository.deleteById(holdings.getId());
        }

        return generateOrderResDTO(order.getId(),OrderStatus.SUCCESS);
    }

    private void validateSymbol(String symbol) {
        symbolRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));
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

    private Order generateOrder(Account account, StockOrderDTO dto, AccIdUserIdInfoDTO infoDto){
        return Order.builder()
                .userId(infoDto.getUserId())
                .accountId(account.getId())
                .price(new BigDecimal(dto.getPrice()))
                .orderType(dto.getOrderType())
                .orderStatus(OrderStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .symbol(dto.getTicker())
                .quantity(dto.getQuantity())
                .totalAmount(calAmount(dto))
                .build();

    }

    private BigDecimal calAmount(StockOrderDTO dto){
        return new BigDecimal(dto.getPrice()).multiply(new BigDecimal(dto.getQuantity()));
    }

    private boolean invalidateBalance(Account account, StockOrderDTO dto) {
        BigDecimal inputPrice = new BigDecimal(Double.toString(dto.getQuantity() * dto.getPrice()));
        // 0은 동일, 1 은 많음, -1은 적음
        return account.getBalance().compareTo(inputPrice) < 0;
    }

    // todo : 종목코드별로 관리 추가 필요
    private boolean invalidateQuantity(StockOrderDTO dto, Holdings holdings){
        return dto.getQuantity() > holdings.getQuantity();
    }

    private Transaction generateTransaction(Account account, Order order, TransactionType transactionType){
        return Transaction.builder()
                .accountId(account.getId())
                .orderId(order.getId())
                .amount(new BigDecimal(String.valueOf(order.getTotalAmount())))
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
