package com.baas.securities.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    HttpStatus status;
    String message;
    Object data;
}
