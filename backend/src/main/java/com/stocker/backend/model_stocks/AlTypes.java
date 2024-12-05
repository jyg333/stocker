package com.stocker.backend.model_stocks;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "al_types")
@Data
@NoArgsConstructor
public class AlTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "al_id", nullable = false)
    private Byte alId;

    @Column(name = "al_type", nullable = false, length = 10)
    private String alType;
}
