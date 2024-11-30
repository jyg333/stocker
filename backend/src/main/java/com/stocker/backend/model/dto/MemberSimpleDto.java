package com.stocker.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//@Getter
//@Setter
//@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSimpleDto {

    private Integer idx;
    private String id;
    private String name;
    private LocalDateTime passwordUpdatedDate;
    private String joinIp;
    private LocalDateTime joinDate;

}

