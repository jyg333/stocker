package com.stocker.backend.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MemberSimpleInfoDto {

    private String id;
    private String name;
    private LocalDateTime passwordUpdatedDate;
    private String joinIp;
    private LocalDateTime joinDate;
    private List<String> roles;

}
