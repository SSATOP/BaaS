package com.baas.bank.account.dto;

public class UserSignupResponse {
    private Long userId;
    private String loginId;
    private String name;

    public UserSignupResponse() {}

    public UserSignupResponse(Long userId, String loginId, String name) {
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
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
}
