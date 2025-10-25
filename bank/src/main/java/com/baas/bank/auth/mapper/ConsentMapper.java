package com.baas.bank.auth.mapper;

import com.baas.bank.oauth2.dto.ConsentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConsentMapper {

    /**
     * 동의 정보를 저장하거나(조합 키가 없으면) 업데이트합니다(있으면).
     */
    void saveOrUpdate(ConsentDto dto);

    /**
     * 복합 키 (클라이언트 ID, 사용자 이름)로 동의 정보를 삭제합니다.
     * * @param registeredClientId 클라이언트 ID
     *
     * @param principalName 사용자 이름
     */
    void deleteByRegisteredClientIdAndPrincipalName(@Param("registeredClientId") String registeredClientId,
                                                    @Param("principalName") String principalName);

    /**
     * 복합 키 (클라이언트 ID, 사용자 이름)로 동의 정보를 조회합니다.
     *
     * @param registeredClientId 클라이언트 ID
     * @param principalName      사용자 이름
     */
    ConsentDto findByRegisteredClientIdAndPrincipalName(@Param("registeredClientId") String registeredClientId,
                                                        @Param("principalName") String principalName);
}