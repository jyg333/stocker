package com.stocker.backend.controller;

import com.stocker.backend.exceptionHandling.ForbiddenException;
import com.stocker.backend.model.dto.response.SearchResultDto;
import com.stocker.backend.model_stocks.StockRegisterDto;
import com.stocker.backend.model_stocks.response.ChartDataResponse;
import com.stocker.backend.repository.MemberFavoriteRepository;
import com.stocker.backend.service.PortfolioService;
import com.stocker.backend.utils.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final MemberFavoriteRepository memberFavoriteRepository;
    private static final Logger logger = LogManager.getLogger(PortfolioController.class);
    private final JwtProvider jwtProvider;

    //Search Bar를 통한 주식 검색
    @GetMapping("/search/{symbol}")
    public ResponseEntity<SearchResultDto> searchStock(@PathVariable("symbol") String symbol) throws IOException {
        //todo : header 검사
        return portfolioService.searchSymbol(symbol);
    }

    // 즐겨찾기 등록
    @PostMapping("/add-favorite")
    public ResponseEntity registerStock(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody StockRegisterDto stockRegisterDto, Errors errors) throws MalformedURLException {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");
//        logger.info(stockRegisterDto);
        boolean result = portfolioService.registerFavorite(stockRegisterDto, id);

        if (result){
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Register Favorite is Success");
        } else {
            //304
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Register Favorite is already exist");
        }
    }
    // Get Favorite List
    @GetMapping("/get-favorite")
    public ResponseEntity getFavorite(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");
        List<String> data = memberFavoriteRepository.findSymbolsById(id);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(data);
    }

    //Graph Data 조회
    @GetMapping("/chart-data/{symbol}")
    public List<ChartDataResponse> getChartData(@PathVariable("symbol") String symbol, @RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
            //403
            logger.error("Invalid Request JWT is weired");
            throw new ForbiddenException("No Permission");
        }
        return portfolioService.getFinanceData(symbol);
    }
    //Comment 남기는 기능

    // 뉴스링크 스르랩 기능 -> nullable


    //삭제
    @DeleteMapping("/delete/{symbol}")
    public ResponseEntity<?> deleteFavorite(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String symbol){
    String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtProvider.validateToken(token)){
        //403
        logger.error("Invalid Request JWT is weired");
        throw new ForbiddenException("No Permission");
        }
        String id = (String) jwtProvider.parseClaims(token).get("userId");
        boolean result = portfolioService.deleteSymbol(id, symbol);

        if (result) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Symbol deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Symbol not found or not authorized.");
        }

    }
}
