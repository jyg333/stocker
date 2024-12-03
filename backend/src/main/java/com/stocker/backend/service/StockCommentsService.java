package com.stocker.backend.service;

import com.stocker.backend.controller.CommentController;
import com.stocker.backend.model_stocks.response.StockCommentUpdateDto;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import com.stocker.backend.model_stocks.response.StockCommentDto;
import com.stocker.backend.model_stocks.StockComments;
import com.stocker.backend.repository.StockCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
public class StockCommentsService {
    private static final Logger logger = LogManager.getLogger(StockCommentsService.class);
    @Autowired
    private StockCommentsRepository stockCommentsRepository;

    // Create: 댓글 추가
    public StockComments createComment(String id, StockCommentDto stockCommentDto) {
        StockComments stockComments = StockComments.builder()
                .id(id)
                .symbol(stockCommentDto.getSymbol())
                .comment(stockCommentDto.getComment())
                .ref(stockCommentDto.getRef())
                .build();

        return stockCommentsRepository.save(stockComments);
    }

    // Read: 특정 Symbol에 대한 댓글 조회 (페이징 + DTO 변환)
    public Page<StockCommentUpdateDto> getCommentsByUserIdAndSymbol(String id, String symbol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return stockCommentsRepository.findByIdAndSymbolOrderByUpdatedAtDesc(id, symbol, pageable)
                .map(sc -> new StockCommentUpdateDto(sc.getComment(), sc.getRef(), sc.getUpdatedAt()));
    }

    // Update: 댓글 수정
    @Transactional
    public boolean updateComment(String symbol, LocalDateTime updatedAt, String newComment, String newReference) {
        int rowsUpdated = stockCommentsRepository.updateCommentBySymbolAndUpdatedAt(newComment, newReference, symbol, updatedAt);
        return rowsUpdated > 0;
    }

    // Delete: 댓글 삭제
    @Transactional
    public boolean deleteComment(String id,String symbol, LocalDateTime updatedAt) {
        logger.info("Delete request from {} {} {}", id,symbol,updatedAt);
        int rowsDeleted = stockCommentsRepository.deleteBySymbolAndUpdatedAt(symbol, updatedAt);
        return rowsDeleted > 0;
    }
}
