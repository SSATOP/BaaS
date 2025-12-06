package com.baas.securities.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data // Getter, Setter, toString 자동 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 생성자
@Builder // 빌더 패턴 사용 가능
public class UserDto {
    private Long id;              // DB PK (AUTO_INCREMENT)
    private String email;         // 이메일 (로그인 ID 역할)
    private String name;          // 사용자 이름
    private String role;          // 권한 (ROLE_USER 등)
    private String oauthProvider; // "BAAS" (어디서 가입했는지)
    // private String password;   // 필요하다면 추가 (OAuth는 보통 불필요)
}