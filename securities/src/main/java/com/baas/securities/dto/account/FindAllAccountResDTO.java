package com.baas.securities.dto.account;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FindAllAccountResDTO {
    private List<FindAccountResDTO> accounts = new ArrayList<>();
}
