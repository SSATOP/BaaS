package com.baas.oauth.mapper;

import com.baas.oauth.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

    /**
     * ID(PK)로 사용자 조회
     *
     * @param id
     * @return User
     */
   Optional<User>  findById(Long id);

    /**
     * Username으로 사용자 조회
     *
     * @param username
     * @return User
     */
    Optional<User> findByUserId(String username);

    /**
     * 사용자 정보 저장 (저장 후 생성된 ID가 User 객체에 자동 매핑됨)
     *
     * @param user
     * @return
     */
    int save(User user);

    /**
     * 사용자 정보 수정
     *
     * @param user
     */
    void update(User user);

    /**
     * ID(PK)로 사용자 삭제
     *
     * @param id
     */
    void deleteById(Long id);
}