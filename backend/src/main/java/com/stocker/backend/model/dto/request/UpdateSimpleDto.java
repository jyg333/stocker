package com.stocker.backend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateSimpleDto {

    @NotEmpty
    private String id;
    private String name;
//    @NotEmpty
//    private String updateUser;
}
