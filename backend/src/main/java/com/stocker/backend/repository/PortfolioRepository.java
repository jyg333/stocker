package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.PortfolioStocks;
import com.stocker.backend.model_stocks.response.ChartDataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<PortfolioStocks, Integer> {

    PortfolioStocks findFirstBySymbolOrderByAccountDateDesc(String symbol);

    @Query("SELECT new com.stocker.backend.model_stocks.response.ChartDataDto(p.eps, p.per, p.roe, p.accountDate) " +
            "FROM PortfolioStocks p WHERE p.symbol = :symbol ORDER BY p.accountDate ASC")
    List<ChartDataDto> findFinancialRatiosBySymbol(String symbol);


}
