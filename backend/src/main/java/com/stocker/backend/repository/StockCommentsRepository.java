package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.StockComments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface StockCommentsRepository extends JpaRepository<StockComments, Integer> {

    // Read: 특정 사용자의 특정 Symbol에 대한 댓글 조회 (페이징)
    Page<StockComments> findByIdAndSymbolOrderByUpdatedAtDesc(String userId, String symbol, Pageable pageable);

    // Update: Symbol과 updatedAt 조건으로 댓글 수정
    @Modifying
    @Query("UPDATE StockComments sc SET sc.comment = :comment, sc.ref = :ref, sc.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE sc.symbol = :symbol AND sc.updatedAt = :updatedAt")
    int updateCommentBySymbolAndUpdatedAt(@Param("comment") String comment, @Param("ref") String ref,
                                          @Param("symbol") String symbol, @Param("updatedAt") LocalDateTime updatedAt);

    // Delete: Symbol과 updatedAt 조건으로 삭제
    @Modifying
    @Query("DELETE FROM StockComments sc WHERE sc.symbol = :symbol AND sc.updatedAt = :updatedAt")
    int deleteBySymbolAndUpdatedAt(@Param("symbol") String symbol, @Param("updatedAt") LocalDateTime updatedAt);
}
