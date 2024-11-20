package com.stocker.backend.repository;

import com.stocker.backend.model.entity.PasswordLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordLogRepository extends JpaRepository<PasswordLog,Integer> {

    @Modifying
    @Query("DELETE FROM PasswordLog where id=:id")
    void deleteByMemberId(@Param("id") String id);

    @Query("SELECT pl FROM PasswordLog pl WHERE pl.id=:id")
    PasswordLog findByMemberId(@Param("id") String id);
}
