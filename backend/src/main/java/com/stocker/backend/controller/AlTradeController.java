package com.stocker.backend.controller;

import com.stocker.backend.exceptionHandling.ForbiddenException;
import com.stocker.backend.exceptionHandling.NotAcceptableException;
import com.stocker.backend.model_stocks.AlTypes;
import com.stocker.backend.model_stocks.request.DeleteTrading;
import com.stocker.backend.model_stocks.request.UpdateAlStatusDto;
import com.stocker.backend.model_stocks.request.UpdateAlTradeDto;
import com.stocker.backend.model_stocks.response.AmountDto;
import com.stocker.backend.model_stocks.response.AmountResponseDto;
import com.stocker.backend.model_stocks.response.ResponseBody;
import com.stocker.backend.model_stocks.response.TradeResultDto;
import com.stocker.backend.repository.AlTypesRepository;
import com.stocker.backend.service.AlTradeResultService;
import com.stocker.backend.utils.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseExtractor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/al-trade")
public class AlTradeController {


    private final AlTradeResultService alTradeResultService;
    private final AlTypesRepository alTypesRepository;


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
    //자동거래 등록
    @PostMapping("/update-settings")
    public ResponseEntity<String> updateAlTradeSettings(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UpdateAlTradeDto updateSettingsDto) {

        // JWT 토큰에서 사용자 ID 추출
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)) {
            logger.error("Invalid JWT token");
            throw new ForbiddenException("Unauthorized access");
        }

        String userId = (String) jwtProvider.parseClaims(token).get("userId");

        // 요청 데이터 처리
        boolean isUpdated = alTradeResultService.registerAlTrade(userId, updateSettingsDto);

        if (isUpdated) {
            logger.info("Al trade register is Successful. ID : {}" ,userId);
            return ResponseEntity.ok("Settings updated successfully");
        } else {
            logger.error("Failed to update settings for userId: {}, symbol: {}", userId, updateSettingsDto.getSymbol());
            throw new NotAcceptableException("Failed to update settings");
        }
    }

    @GetMapping("/al-types")
    public List<String> getAltypes(@RequestHeader("Authorization") String authorizationHeader){

        List<String> response = alTradeResultService.getAllTypes();

        return response;
    }

    // Al-trade page의 SideBar에서 Delete button을 클릭했을 때, al-trade talbe, al-trade result table을 삭제해주는 메서드실행
    @PostMapping("/delete/trading")
    public ResponseEntity deleteTrading(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody DeleteTrading deleteTrading){
        String token = authorizationHeader.replace("Bearer ", "");
        String userId = (String) jwtProvider.parseClaims(token).get("userId");

        boolean result = alTradeResultService.deleteAlTrade(userId, deleteTrading);

        ResponseBody responseBody = new ResponseBody();
        if(result){
            responseBody.setMessage("Success Delete");
            //202
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
        }else {
            responseBody.setMessage("Fail Delete");
            //406
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseBody);
        }
    }

}
