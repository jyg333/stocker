package com.stocker.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
//    private Integer code;
    private String message;
}
