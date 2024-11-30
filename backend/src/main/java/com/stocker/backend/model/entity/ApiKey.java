package com.stocker.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table(name = "api_key")
@Entity
@AllArgsConstructor
@Builder
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // `idx`가 자동 증가하는 경우 사용
    private Integer idx;

    @Column(name = "key_string", nullable = false, length = 40)
    private String key_string;

    @Column(name = "expiredAt", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "requester", length = 20)
    private String requester;
}
