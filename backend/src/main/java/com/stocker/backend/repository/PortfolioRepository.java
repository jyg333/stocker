package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.PortfolioStocks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<PortfolioStocks, Integer> {

    PortfolioStocks findFirstBySymbolOrderByAccountDateDesc(String symbol);
}
