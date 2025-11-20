package com.baas.bank.account.mapper;

import com.baas.bank.account.dto.AccountDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    void insertAccount(AccountDto account);
    
    AccountDto findById(Long id);
    
    AccountDto findByAccountNumber(String accountNumber);
    
    List<AccountDto> findByUserId(Long userId);
    
    void updateAlias(@Param("id") Long id, @Param("alias") String alias);
    
    boolean existsByAccountNumber(String accountNumber);
}

