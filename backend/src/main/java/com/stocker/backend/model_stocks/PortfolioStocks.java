package com.stocker.backend.model_stocks;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Entity
@Builder
@NoArgsConstructor
@Data
@Table(name = "portfolio_stocks")
public class PortfolioStocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "stock_id", nullable = true)
    private Integer stockId;

    @Column(name = "symbol", length = 10, nullable = false)
    private String symbol;

    @Column(name = "account_date", nullable = false)
    private LocalDate accountDate;

    @Column(name = "eps")
    private Float eps;

    @Column(name = "per")
    private Float per;

    @Column(name = "net_income_ratio")
    private Float netIncomeRatio;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
