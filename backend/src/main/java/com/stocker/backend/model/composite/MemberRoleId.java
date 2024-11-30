package com.stocker.backend.model.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.IdClass;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

// Create an embeddable class to represent the composite key
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@IdClass(MemberRoleId.class)
public class MemberRoleId implements Serializable {

    @Column(name = "idx")
    private Integer idx;

    @Column(name = "roles_id")
    private Integer rolesId;

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberRoleId that = (MemberRoleId) o;
        return Objects.equals(idx, that.getIdx()) && Objects.equals(rolesId, that.getRolesId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(idx, rolesId);
    }
}
