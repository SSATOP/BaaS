package com.baas.bank.auth.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "emailVerification", timeToLive = 300)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerify {
    @Id
    private String email;
    private String code;
}
