package com.stocker.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class MemberAllListDto {

    private String id;
    private String position;
    private Boolean activation;
    private Integer failCount;
    private List<String> roles;

}

