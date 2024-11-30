package com.stocker.backend.model.stocks;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class StockRegisterDto {

    @NotEmpty
    private String id;
    private String stock_name;
    private String stock_code;

}