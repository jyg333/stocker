package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.MemberFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberFavoriteRepository  extends JpaRepository<MemberFavorite, Integer> {

    List<MemberFavorite> findById(String id);

    MemberFavorite findMemberFavoritesByIdAndSymbol(String id, String symbol);

    @Query("SELECT mf.symbol FROM MemberFavorite mf WHERE mf.id = :id")
    List<String> findSymbolsById(@Param("id") String id);
}
