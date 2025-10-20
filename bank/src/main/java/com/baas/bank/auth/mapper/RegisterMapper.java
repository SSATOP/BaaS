package com.baas.bank.auth.mapper;

import com.baas.bank.oauth2.dto.RegisterDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegisterMapper {
    /**
     * 클라이언트 정보를 저장하거나(ID가 없으면) 업데이트합니다(ID가 있으면).
     */
    void saveOrUpdate(RegisterDto dto);

    /**
     * Spring Security의 RegisteredClient 고유 ID로 조회합니다.
     */
    RegisterDto findById(String id);

    /**
     * 클라이언트 ID(client_id)로 조회합니다.
     */
    RegisterDto findByClientId(String clientId);
}