package com.stocker.backend.repository;


import com.stocker.backend.model_stocks.AlTrade;
import com.stocker.backend.model_stocks.response.AmountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlTradeRepository extends JpaRepository<AlTrade, Integer> {

    @Query("""
           SELECT new com.stocker.backend.model_stocks.response.AmountDto(at.initAmount, at.curAmount)
           FROM AlTrade at
           WHERE at.symbol = :symbol
           """)
    List<AmountDto> findBySymbolInitAndCurAmount(@Param("symbol") String symbol, @Param("al_id") Integer alId);
}

