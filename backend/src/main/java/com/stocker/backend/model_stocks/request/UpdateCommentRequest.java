package com.stocker.backend.model_stocks.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {
    private String symbol;
    private LocalDateTime updatedAt;
    private String newComment;
    private String newReference;

}

