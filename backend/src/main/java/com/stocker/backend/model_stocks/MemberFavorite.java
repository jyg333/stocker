package com.stocker.backend.model_stocks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_favorite")
public class MemberFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx; // Primary Key, Auto-Increment

    @Column(name = "id", length = 20, nullable = false)
    private String id; // 멤버 ID (VARCHAR(20), NOT NULL)

    @Column(name = "symbol", length = 10, nullable = false)
    private String symbol; // 주식 Symbol (VARCHAR(10), NOT NULL)

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 업데이트 시간 (DATETIME, NULL 가능)

}
