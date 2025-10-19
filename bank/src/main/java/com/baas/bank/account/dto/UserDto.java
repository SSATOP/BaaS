package com.baas.bank.account.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;                // BIGINT AUTO_INCREMENT
    private String loginId;         // login_id
    private String loginPassword;   // login_password
    private String name;            // name
    private LocalDate birthDate;// birth_date
    private String phone;           // phone
    private String email;           // email
    private LocalDateTime createdAt;// created_at

    public UserDto( ) {}

    public UserDto(Long id, String loginId, String loginPassword, String name, LocalDate birthDate, String phone, String email, LocalDateTime createdAt) {
        this.id = id;
        this.loginId = loginId;
        this.loginPassword = loginPassword;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
