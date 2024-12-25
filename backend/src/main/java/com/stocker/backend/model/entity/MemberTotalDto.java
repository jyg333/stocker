package com.stocker.backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberTotalDto {


    private String id;
    private String name;
    private Boolean activation;
    private Integer failCount;
    private LocalDateTime fail_dt; // 가장최근 password 실패시간
    private LocalDateTime join_dt; // 가장최근 로그인 시간
    private String join_ip;
    private LocalDateTime create_dt; // 계정 생성일
    private String created_by; // 계정 생성자
    private LocalDateTime updated_dt;
    private String updated_by;
    private List<String> roles;
}
