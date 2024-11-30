package com.stocker.backend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateDetailDto {

    @NotEmpty
    private String id;
    @NotNull
    private boolean activation;
    @NotNull
    private Integer failCount;
    @NotEmpty
    private List<String> roles;
    @NotEmpty
    private String updateUser;

    private String name;
    private String ipAddress;

}
