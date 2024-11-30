package com.stocker.backend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {

    private Integer idx;
    private String id;
    private String position;
    private Boolean activation;
    private String name;


}

