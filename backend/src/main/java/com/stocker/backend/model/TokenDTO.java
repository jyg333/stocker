package com.stocker.backend.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenDTO {

//    private String grantType;
    private String accessToken;
//    private Date refreshTokenExpDate;
    private String refreshToken;


}
