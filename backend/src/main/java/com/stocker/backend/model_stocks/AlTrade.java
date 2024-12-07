package com.stocker.backend.model_stocks;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "al_trade")
@Data
@NoArgsConstructor
public class AlTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Column(name = "symbol", nullable = false, length = 10)
    private String symbol;

    @ManyToOne
    @JoinColumn(name = "al_id", referencedColumnName = "al_id", nullable = false)
    private AlTypes alId;

    @Column(name = "init_amount", nullable = false)
    private Long initAmount;

    @Column(name = "cur_amount", nullable = false)
    private Long curAmount;

    @Column(name = "lower_limit")
    private Long lowerLimit;

    @Column(name = "upper_limit")
    private Long upperLimit;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "activation", nullable = false)
    private Boolean activation;
}