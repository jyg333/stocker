package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.StockVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockVolumeRepository extends JpaRepository<StockVolume, Integer> {

    // 필요한 경우 추가 메서드 정의 가능
    StockVolume findBySymbolAndVolDate(String symbol, java.time.LocalDate volDate);
}
