package com.stocker.backend.model_stocks.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAlTradeDto {

    @NotNull
    private String symbol;

    @NotNull
    @Positive
    private Long initAmount;

    @Nullable
    @Positive
    private Long upperLimit;

    @Nullable
    private Long lowerLimit;

    private String endAt; // Optional: ISO 8601 형식의 문자열로 처리

    @NotNull
    private String alType; // 새로 추가된 필드: 알고리즘 유형

    // Getters and Setters
}
