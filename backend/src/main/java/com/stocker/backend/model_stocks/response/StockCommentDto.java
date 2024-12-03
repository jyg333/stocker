package com.stocker.backend.model_stocks.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockCommentDto {
    private String symbol;
    private String comment;
    private String ref;
}