package com.stocker.backend.controller;


import com.stocker.backend.model.dto.response.InspectionDto;
import com.stocker.backend.model.entity.Inspection;
import com.stocker.backend.repository.InspectionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inspection")
@RequiredArgsConstructor
public class InspectionController {


    private final InspectionRepository inspectionRepository;

    @GetMapping("/list")
    public List<Inspection> getAllInspection(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request,
                                                @RequestParam(value = "limit", required = false) Integer limit,
                                                @RequestParam(value = "page", required = false) Integer page){

        if (limit == null && page == null){
            Pageable pageable = PageRequest.of(0, 10000);
//            List<InspectionDto> inspections = inspectionRepository.findByPlatform(pageable);
            List<Inspection> inspections = inspectionRepository.findAll();
            return inspections;
        }
//        else {
//            Sort sort = Sort.by("savedAt").descending();
//            Pageable pageable = PageRequest.of(page, limit, sort);
//            List<InspectionDto> inspections = inspectionRepository.findByPlatformWithPage(pageable);
//            return inspections;
//        }
        return null;
    }

    @GetMapping("/count")
    public Integer getCount(HttpServletRequest request){

        return inspectionRepository.countAll(10000);
    }


}
