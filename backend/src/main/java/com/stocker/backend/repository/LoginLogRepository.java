package com.stocker.backend.repository;

import com.stocker.backend.model.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginLogRepository extends JpaRepository<LoginLog, Integer> {

    @Modifying
    @Query("DELETE FROM LoginLog WHERE id = :id")
    void deleteLoginLogById(@Param("id") String id);

    @Query("SELECT ll FROM LoginLog ll WHERE ll.id=:id")
    LoginLog findLoginLogByMemberId(@Param("id") String id);
}
