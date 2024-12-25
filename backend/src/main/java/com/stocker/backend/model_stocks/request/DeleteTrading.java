package com.stocker.backend.model_stocks.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTrading {

    private String symbol;
//    private String id;
}
