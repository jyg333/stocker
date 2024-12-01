package com.stocker.backend.model_stocks.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChartDataResponse {
    private String symbol; // 예: EPS, PER, Net Income Ratio
    private List<PriceData> prices;

    @Data
    @AllArgsConstructor
    public static class PriceData {
        private String date; // 예: 2024-09-28
        private Float price; // 데이터 값 (null 허용)
    }
}

