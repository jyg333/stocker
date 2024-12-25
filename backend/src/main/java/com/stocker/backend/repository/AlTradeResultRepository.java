package com.stocker.backend.repository;


import com.stocker.backend.model_stocks.AlTrade;
import com.stocker.backend.model_stocks.AlTradeResult;
import com.stocker.backend.model_stocks.response.TradeResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlTradeResultRepository extends JpaRepository<AlTradeResult, Integer> {

    // 특정 거래(AlTrade)와 관련된 결과 조회
//    List<AlTradeResult> findByTradeIdx(Integer tradeIdx);

    @Query(value = """
       SELECT DATE(traded_at) AS trade_date, profit_loss
       FROM al_trade_result atr
       WHERE atr.symbol = :symbol
       AND atr.traded_at IN (
           SELECT MAX(subAtr.traded_at)
           FROM al_trade_result subAtr
           WHERE subAtr.symbol = :symbol
           GROUP BY DATE(subAtr.traded_at)
       )
       """, nativeQuery = true)
    List<TradeResultDto> findLatestTradeResultsBySymbol(@Param("symbol") String symbol);

    void deleteAllByTradeIdxAndSymbol(AlTrade idx, String symbol);
//    void deleteAllByIdxAndSymbol(Integer idx, String symbol);
}
