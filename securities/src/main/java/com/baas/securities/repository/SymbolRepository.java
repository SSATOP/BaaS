package com.baas.securities.repository;

import com.baas.securities.repository.entity.Symbol;

import java.util.Optional;

public interface SymbolRepository {
    Optional<Symbol> findBySymbol(String symbol);

    void save(Symbol symbol);
}
