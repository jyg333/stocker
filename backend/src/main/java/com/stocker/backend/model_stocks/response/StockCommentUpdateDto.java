package com.stocker.backend.model_stocks.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockCommentUpdateDto {

    private String comment;
    private String ref;
    private LocalDateTime updatedAt;
}
