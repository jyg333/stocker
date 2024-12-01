package com.stocker.backend.model_stocks.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDto {
    private Float eps;
    private Float per;
    private Float netIncomeRatio;
    private LocalDate accountDate;
}
