package com.stocker.backend.model_stocks.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountDto {

    private Long initAmount;
    private Long curAmount;
    private Long buyAmount;

}
