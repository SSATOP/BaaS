package com.baas.oauth.mapper;

import com.baas.oauth.model.Consent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConsentMapper {

    /**
     * 복합키로 동의 정보 조회
     * @param registeredClientId
     * @param principalName
     * @return Consent
     */
    Consent findById(@Param("registeredClientId") String registeredClientId,
                     @Param("principalName") String principalName);

    /**
     * 동의 정보 저장
     * @param consent
     */
    void save(Consent consent);

    /**
     * 동의 정보 수정
     * @param consent
     */
    void update(Consent consent);

    /**
     * 복합키로 동의 정보 삭제
     * @param registeredClientId
     * @param principalName
     */
    void deleteById(@Param("registeredClientId") String registeredClientId,
                    @Param("principalName") String principalName);
}