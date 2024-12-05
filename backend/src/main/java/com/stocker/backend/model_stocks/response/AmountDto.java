package com.stocker.backend.model_stocks.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AmountDto {

    private Long initAmount;
    private Long curAmount;

}
