package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.MemberFavorite;
import com.stocker.backend.model_stocks.response.MemberFavoriteDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberFavoriteRepository  extends JpaRepository<MemberFavorite, Integer> {

    List<MemberFavorite> findById(String id);

    MemberFavorite findMemberFavoritesByIdAndSymbol(String id, String symbol);

    @Query("SELECT mf.symbol FROM MemberFavorite mf WHERE mf.id = :id")
    List<String> findSymbolsById(@Param("id") String id);

    //with al_status
    @Query("SELECT m.symbol AS symbol, m.alStatus AS alStatus FROM MemberFavorite m WHERE m.id = :id")
    List<MemberFavoriteDto> findByIdWithSymbolAndStatus(String id);

    MemberFavorite findByIdAndSymbol(String id, String symbol);

    @Modifying
    @Transactional
    @Query("DELETE FROM MemberFavorite mf WHERE mf.id = :id AND mf.symbol = :symbol")
    int deleteByIdAndSymbol(@Param("id") String id, @Param("symbol") String symbol);
}
