package com.stocker.backend.controller;

import com.stocker.backend.model_stocks.response.AmountDto;
import com.stocker.backend.model_stocks.response.TradeResultDto;
import com.stocker.backend.service.AlTradeResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseExtractor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/al-trade")
public class AlTradeController {


    private final AlTradeResultService alTradeResultService;

    //수익율 번화율
    @GetMapping("/trade-results")
    public ResponseEntity<List<TradeResultDto>> getLatestTradeResults(@RequestParam String symbol) {
        List<TradeResultDto> results = alTradeResultService.getLatestTradeResults(symbol);
        return ResponseEntity.ok(results);
    }

    //price data
    @GetMapping("/trade-amount")
    public ResponseEntity<AmountDto> getAmount(@RequestParam String symbol){

        return alTradeResultService.
    }
    //거래량 변화율
}
