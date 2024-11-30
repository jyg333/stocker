package com.stocker.backend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

/*Password update 기능 Dto*/
@Getter
public class UpdatePasswordDto {

    @NotEmpty
    private String currentPassword;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    private String validPassword;
}
