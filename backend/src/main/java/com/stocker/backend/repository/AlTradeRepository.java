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


    //무결성 관리를 위한 alId 선언에 따른 parameter 수정
    @Query("""
           SELECT new com.stocker.backend.model_stocks.response.AmountDto(at.initAmount, at.curAmount, at.buyAmount)
           FROM AlTrade at
           WHERE at.symbol = :symbol
           AND at.alId.alId = :alId
           AND at.id=:id
           """)
    AmountDto findBySymbolInitAndCurAmount(@Param("symbol") String symbol, @Param("alId") Byte alId, @Param("id")String id);

    AlTrade findByIdAndSymbol(String id, String symbol);


}

