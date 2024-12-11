package com.stocker.backend.model.dto.request;

import jakarta.validation.constraints.Email;
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
    private String password;
    @NotEmpty
    private String validPassword;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email // 이메일 형식 검증
    private String email;
}
