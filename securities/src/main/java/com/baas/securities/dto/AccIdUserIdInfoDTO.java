package com.baas.securities.dto;

import com.baas.securities.repository.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccIdUserIdInfoDTO {
    private String userId;
    private String accountId;

    public static AccIdUserIdInfoDTO generate(Account account) {
        return new AccIdUserIdInfoDTO(account.getUserId(), account.getId());
    }
}
