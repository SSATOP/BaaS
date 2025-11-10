package com.baas.oauth.mapper;

import com.baas.oauth.model.Register;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RegisterMapper {

    /**
     * ID로 클라이언트 정보 조회
     *
     * @param id
     * @return Register
     */
    Optional<Register> findById(String id);

    /**
     * Client ID로 클라이언트 정보 조회
     * (Spring Security OAuth2에서 주로 사용)
     *
     * @param clientId
     * @return Register
     */
    Optional<Register> findByClientId(String clientId);

    /**
     * 클라이언트 정보 저장
     *
     * @param register
     */
    void save(Register register);

    /**
     * 클라이언트 정보 수정
     *
     * @param register
     */
    void update(Register register);

    /**
     * ID로 클라이언트 정보 삭제
     *
     * @param id
     */
    void deleteById(String id);
}