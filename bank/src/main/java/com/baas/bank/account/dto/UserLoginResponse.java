package com.baas.bank.account.dto;

public class UserLoginResponse {
    private Long userId;
    private String loginId;
    private String name;
    private String message;

    public UserLoginResponse() {}

    public UserLoginResponse(Long userId, String loginId, String name, String message) {
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
