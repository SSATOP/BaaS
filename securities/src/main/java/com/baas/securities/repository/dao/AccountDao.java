package com.baas.securities.repository.dao;

import com.baas.securities.repository.AccountRepository;
import com.baas.securities.repository.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDao extends AccountRepository {
    void update(Account account); // 잔액 업데이트
}
