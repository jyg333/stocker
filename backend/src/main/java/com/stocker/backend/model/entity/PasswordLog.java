package com.stocker.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_log")
public class PasswordLog {

    @Id
    @Column(name = "idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;


    @Column(name = "id", nullable = false)
    private String id;

    @Size(max = 255)
    @Column(name = "last_password")
    private String lastPassword;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // Define the relationship to the Member entity
//    @ManyToOne
//    @JoinColumn(name = "member_id", insertable = false, updatable = false)
//    private Member member;
}
