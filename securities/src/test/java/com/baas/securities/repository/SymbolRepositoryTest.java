package com.baas.securities.repository;

import com.baas.securities.repository.entity.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SymbolRepositoryTest {

    @Autowired
    SymbolRepository symbolRepository;

    @Test
    @DisplayName("symbol 생성")
    void createSymbol() {
        // given
        Symbol symbol = Symbol.builder()
                .symbol("000660")
                .name("SK하이닉스")
                .build();

        // when
        symbolRepository.save(symbol);
        // then
    }
}