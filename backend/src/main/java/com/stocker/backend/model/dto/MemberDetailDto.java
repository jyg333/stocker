package com.stocker.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDetailDto {

    private Integer idx;
    private String id;
    private String name;

    private Boolean activation;
    private LocalDateTime passwordUpdatedDt; //password 변경시간
    private Integer failCount;
    private LocalDateTime failDt; // 가장최근 password 실패시간
    private String joinIp;
    private LocalDateTime joinDt; // 가장최근 로그인 시간
    private LocalDateTime createdDt; // 계정 생성일
    private String createdBy; // 계정 생성자
    private LocalDateTime updatedDt;
    private String updatedBy;
}
