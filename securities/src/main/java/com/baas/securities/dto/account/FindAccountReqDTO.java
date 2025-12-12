package com.baas.securities.dto.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindAccountReqDTO {
    private String accountId;
    // 나중에 비밀번호 인증을 하고나서 서버에서 발급되는 코드를 요청할때 다시 보내는 식으로 수정필요.
    private String accountPassword;
}
