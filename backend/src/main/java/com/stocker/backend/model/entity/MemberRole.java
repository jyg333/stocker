package com.stocker.backend.model.entity;

import com.stocker.backend.model.composite.MemberRoleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(MemberRoleId.class)
public class MemberRole {


        @Id
        @Column(name = "idx")
        private Integer idx;

        @Column(name = "roles_id", nullable = false)
        private Integer rolesId;



        @ManyToOne
        @JoinColumn(name = "idx", referencedColumnName = "idx", insertable = false, updatable = false)
        private Member member;

        @ManyToOne
        @JoinColumn(name = "roles_id", referencedColumnName = "roles_id", insertable = false, updatable = false)
        private RoleInfo rolesInfo;
    }



