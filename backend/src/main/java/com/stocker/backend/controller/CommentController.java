package com.stocker.backend.controller;


import com.stocker.backend.exceptionHandling.ForbiddenException;
import com.stocker.backend.model_stocks.request.UpdateCommentRequest;
import com.stocker.backend.model_stocks.response.StockCommentUpdateDto;
import com.stocker.backend.utils.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stocker.backend.model_stocks.response.StockCommentDto;
import com.stocker.backend.model_stocks.StockComments;
import com.stocker.backend.service.StockCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final JwtProvider jwtProvider;
    private static final Logger logger = LogManager.getLogger(CommentController.class);
    @Autowired
    private StockCommentsService stockCommentsService;

    // Create: 댓글 추가
    @PostMapping
    public StockComments createComment(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody StockCommentDto stockCommentDto) {

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");

        return stockCommentsService.createComment(id, stockCommentDto);
    }

    // Read: 특정 사용자의 특정 Symbol에 대한 댓글 조회 (페이징 + DTO 반환)
    @GetMapping
    public Page<StockCommentUpdateDto> getCommentsByUserIdAndSymbol(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String symbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");
        return stockCommentsService.getCommentsByUserIdAndSymbol(id, symbol, page, size);
    }

    // Update: 댓글 수정
    @PutMapping
    public boolean updateComment(@RequestHeader("Authorization") String authorizationHeader,
                                 @RequestBody UpdateCommentRequest request) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)) {
            throw new ForbiddenException("No Permission");
        }

        String id = (String) jwtProvider.parseClaims(token).get("userId");
        logger.info("Update request from {} for symbol: {} with updatedAt: {}, newComment: {}, newReference: {}",
                id, request.getSymbol(), request.getUpdatedAt(), request.getNewComment(), request.getNewReference());

        return stockCommentsService.updateComment(
                request.getSymbol(),
                request.getUpdatedAt(),
                request.getNewComment(),
                request.getNewReference()
        );
    }


    // Delete: 댓글 삭제
    @DeleteMapping
    public boolean deleteComment(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String symbol,
            @RequestParam LocalDateTime updatedAt) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }

        String id = (String) jwtProvider.parseClaims(token).get("userId");
        logger.info("Delete request from {} {} {}", id,symbol,updatedAt);

        return stockCommentsService.deleteComment(id,symbol, updatedAt);
    }
}
