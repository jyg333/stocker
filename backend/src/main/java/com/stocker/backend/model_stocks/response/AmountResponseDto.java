package com.stocker.backend.model_stocks.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountResponseDto {

    private Long initAmount;
    private Long curAmount;
    private Double ratio;

}
