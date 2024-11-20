package com.stocker.backend.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "login_log")
public class LoginLog {

    @Id
    @Column(name = "idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @NotNull
    @Size(max = 20)
    @Column(name = "id", nullable = false)
    private String id;


    @ColumnDefault("0")
    @Column(name = "fail_count", nullable = false)
    private Integer failCount;

    @Column(name = "fail_dt")
    private LocalDateTime failDt;


    @Column(name = "join_ip")
    private String joinIp;

    @Column(name = "join_dt")
    private LocalDateTime joinDt;

    @NotNull
    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Define the relationship to the Member entity
    @ManyToOne
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;
}
