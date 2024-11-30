package com.stocker.backend.repository;

import com.stocker.backend.model.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {
    @Query("SELECT a.key_string FROM ApiKey a WHERE a.key_string=:key_string")
    String findByKey_string(@Param("key_string") String id);
}
