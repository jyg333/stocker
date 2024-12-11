package com.stocker.backend.repository;

import com.stocker.backend.model_stocks.AlTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlTypesRepository extends JpaRepository<AlTypes, Integer> {

    AlTypes findByAlType(String al_type);

    List<AlTypes> findAll();
}
