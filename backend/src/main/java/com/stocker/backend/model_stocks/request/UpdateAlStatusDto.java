package com.stocker.backend.model_stocks.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAlStatusDto {

    private String symbol;
    private Boolean al_status;
}
