package com.stocker.backend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PasswordResetDto {

    @NotEmpty
    private String id;

}
