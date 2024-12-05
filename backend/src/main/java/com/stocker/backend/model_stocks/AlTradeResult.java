package com.stocker.backend.model_stocks;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "al_trade_result")
@Data
@NoArgsConstructor
public class AlTradeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne
    @JoinColumn(name = "trade_idx", referencedColumnName = "idx", nullable = false)
    private AlTrade trade;

    @Column(name = "symbol", nullable = false)
    private String symbol; // 1: Buy, 2: Sell, 3: Hold

    @Column(name = "trade_type", nullable = false)
    private Byte tradeType; // 1: Buy, 2: Sell, 3: Hold

    @Column(name = "trade_amount")
    private Long tradeAmount;

    @Column(name = "trade_price")
    private Long tradePrice;

    @Column(name = "profit_loss")
    private Long profitLoss;

    @Column(name = "traded_at", nullable = false)
    private LocalDateTime tradedAt;
}
