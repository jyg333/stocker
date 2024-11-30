package com.stocker.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordResetResponseDto {

    private String tempPassword;
}
