package com.stocker.backend.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "roles_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roles_id")
    private Integer rolesId;

    @Column(name = "roles_name", length = 20)
    private String rolesName;

    @OneToMany(mappedBy = "rolesInfo")
    private Set<MemberRole> memberRoles;

}
