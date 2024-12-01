package com.stocker.backend.model_stocks;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockRegisterDto {

    @NotEmpty
    private String symbol;

}