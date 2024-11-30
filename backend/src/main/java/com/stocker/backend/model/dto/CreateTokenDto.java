package com.stocker.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTokenDto {

    private Integer memberIdx;
    private String name;
    private Authentication authentication;
    private List<String> roles;

}
