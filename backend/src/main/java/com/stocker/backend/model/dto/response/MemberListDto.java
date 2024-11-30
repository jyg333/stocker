package com.stocker.backend.model.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemberListDto {

    private String id;
    private String name;
    private List<String> roles;

    public MemberListDto(String id, String name, List<String> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }
}

