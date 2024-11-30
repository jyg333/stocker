package com.stocker.backend.model.composite;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.IdClass;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@IdClass(MemberPlatformId.class)
public class MemberPlatformId implements Serializable {
    @Column(name = "idx")
    private Integer idx;

    @Column(name = "platform_id")
    private Integer platformId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberPlatformId that = (MemberPlatformId) o;
        return Objects.equals(idx, that.getIdx()) && Objects.equals(platformId, that.getPlatformId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(idx, platformId);
    }
}
