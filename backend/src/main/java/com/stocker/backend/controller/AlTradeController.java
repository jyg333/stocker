package com.stocker.backend.controller;

import com.stocker.backend.exceptionHandling.ForbiddenException;
import com.stocker.backend.exceptionHandling.NotAcceptableException;
import com.stocker.backend.model_stocks.request.UpdateAlStatusDto;
import com.stocker.backend.model_stocks.response.AmountDto;
import com.stocker.backend.model_stocks.response.AmountResponseDto;
import com.stocker.backend.model_stocks.response.TradeResultDto;
import com.stocker.backend.service.AlTradeResultService;
import com.stocker.backend.utils.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseExtractor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/al-trade")
public class AlTradeController {


    private final AlTradeResultService alTradeResultService;
    private final JwtProvider jwtProvider;
    private static final Logger logger = LogManager.getLogger(AlTradeController.class);


    //수익율 번화율
    @GetMapping("/trade-results")
    public ResponseEntity<List<TradeResultDto>> getLatestTradeResults(@RequestParam String symbol) {
        List<TradeResultDto> results = alTradeResultService.getLatestTradeResults(symbol);
        return ResponseEntity.ok(results);
    }

    //price data
    @GetMapping("/trade-amount")
    public AmountResponseDto getAmount(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String symbol){

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");
        return alTradeResultService.getAmount(symbol, id);
//        return alTradeResultService.
    }
    //거래량 변화율

    //update al_status
    @PutMapping("/set-status")
    public ResponseEntity updateAlStatus(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody UpdateAlStatusDto updateAlStatusDto){

        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");
        boolean result = alTradeResultService.updateFavorite(id, updateAlStatusDto);
        if (result) {
            // 200 OK 반환
            return ResponseEntity.ok("Update successful");
        } else {
            // 500 Internal Server Error 반환
            logger.error("Register algorithm trade fail Id : {}, symbol : {}", updateAlStatusDto.getSymbol(), updateAlStatusDto.getSymbol());
            throw new NotAcceptableException("Register algorithm trade fail");

        }
    }
}
