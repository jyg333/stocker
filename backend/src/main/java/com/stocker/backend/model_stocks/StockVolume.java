package com.stocker.backend.model_stocks;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_volume")
@Data
@NoArgsConstructor
public class StockVolume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "symbol", nullable = false, length = 10)
    private String symbol;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Column(name = "vol_date", nullable = false)
    private LocalDate volDate;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
