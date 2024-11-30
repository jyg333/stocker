package com.stocker.backend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterDto {

    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    @NotNull
    private boolean activation; // 계정을 생성할 때 필요한가?
    @NotEmpty
    private List<String> roles;
    @NotEmpty
    private String createdBy;
}
