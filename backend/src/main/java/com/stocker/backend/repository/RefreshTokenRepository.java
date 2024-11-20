package com.stocker.backend.repository;


import com.stocker.backend.model.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String key);


    RefreshToken findOneByToken(String token);
    RefreshToken findOneById(String id);

    @Modifying
    @Query("DELETE FROM RefreshToken WHERE id=:id")
    void deleteById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.token =:token, rt.expiredAt=:expiredAt WHERE rt.idx =:idx ")
    void updateRefreshToken(@Param("idx") Integer idx, @Param("token") String token, @Param("expiredAt")LocalDateTime expiredAt);
}
