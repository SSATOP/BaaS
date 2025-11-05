package com.baas.securities.repository.dao;

import com.baas.securities.repository.UserRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper
public interface UserDao extends UserRepository {
}
