package com.stocker.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "member",uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
@Entity
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(name = "idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String id;

    @Column(length = 255)
    private String password;


    @Column(name = "activation", columnDefinition = "TINYINT(1) DEFAULT 0", nullable = false)
    private Boolean activation;

    @Size(max = 10)
    @Column(name = "name", length = 10)
    private String name;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberRole> memberRoles;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LoginLog> loginLogs;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RefreshToken> refreshTokens;

}