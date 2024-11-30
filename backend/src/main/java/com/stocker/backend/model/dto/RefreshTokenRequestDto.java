package com.stocker.backend.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDto {

    @NotEmpty
    private String id;
    @NotEmpty
    private String token;
}
