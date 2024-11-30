package com.stocker.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InspectionDto {

    private String content;

    private LocalDateTime savedAt;
}
