package com.stocker.backend.service;

import com.stocker.backend.model.dto.response.InspectionDto;
import com.stocker.backend.model.entity.Inspection;
import com.stocker.backend.repository.InspectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InspectionService {
    private final InspectionRepository inspectionRepository;

//    public List<Inspection> getAllInspections(String platform) {
//        Pageable pageable = PageRequest.of(0, 10000);
//        List<Inspection> inspections = inspectionRepository.findByPlatform(platform, pageable);
//        return inspections;
//    }

}
