package com.stocker.backend.repository;


import com.stocker.backend.model.dto.response.InspectionDto;
import com.stocker.backend.model.entity.Inspection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

//    @Query("SELECT new com.stocker.backend.model.dto.response.InspectionDto(i.content, i.savedAt) " +
//            "FROM Inspection i WHERE i.platform = :platform ORDER BY i.savedAt DESC")
//    List<InspectionDto> findByPlatform(@Param("platform") String platform, Pageable pageable);
//
//    @Query(value="SELECT new com.stocker.backend.model.dto.response.InspectionDto(i.content, i.savedAt)  " +
//            "FROM Inspection i WHERE i.platform = :platform ORDER BY i.savedAt DESC")
//    List<InspectionDto> findByPlatformWithPage(@Param("platform") String platform, Pageable pageable);



    @Query(value = "SELECT COUNT(*) FROM inspection LIMIT :limit", nativeQuery = true)
    Integer countAll(@Param("limit") int limit);

}
