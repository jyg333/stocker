package com.stocker.backend.model_stocks.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//@Data
//@NoArgsConstructor
////@AllArgsConstructor
//public class TradeResultDto {
//    private LocalDate tradeDate; // 거래 날짜
//    private Long profitLoss;     // 손익
//
//    // 명시적 생성자 추가
//    public TradeResultDto(LocalDate tradeDate, Long profitLoss) {
//        this.tradeDate = tradeDate;
//        this.profitLoss = profitLoss;
//    }
//
//}
public interface TradeResultDto {
    LocalDate getTradeDate();
    Long getProfitLoss();
}
