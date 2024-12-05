package com.stocker.backend.model_stocks.response;

import lombok.Data;

@Data
public class AmountResponseDto {

    private Long initAmount;
    private Long curAmount;
    private Double calculateChangeRate;

}
