package com.stocker.backend.model_stocks;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "stock_comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Column(name = "symbol", nullable = false, length = 10)
    private String symbol;

    @Column(name = "comment", nullable = false, length = 450)
    private String comment;

    @Column(name = "ref", length = 300)
    private String ref;

    @Column(
            name = "updated_at",
            nullable = false,
            updatable = false,
            insertable = false, // Hibernate가 기본값 설정을 건드리지 않게 함
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updatedAt;

}
