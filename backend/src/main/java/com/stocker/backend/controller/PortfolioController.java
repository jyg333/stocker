package com.stocker.backend.controller;

import com.stocker.backend.model.dto.request.UpdateDetailDto;
import com.stocker.backend.model.stocks.StockRegisterDto;
import com.stocker.backend.service.PortfolioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    //Search Bar를 통한 주식 검색
    @GetMapping("/search/{symbol}")
    public EntityResponse<String> searchStock(@PathVariable("symbol") String symbol) throws IOException {
        System.out.println("Symbol : "+symbol);

        portfolioService.searchSymbol(symbol);
        return null;
    }

    // 즐겨찾기 등록
    @PostMapping("/add-favorite")
    public HttpStatus registerStock(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody StockRegisterDto stockRegisterDto, Errors errors, HttpServletRequest request) throws MalformedURLException {

        boolean result = portfolioService.registerFavorite(stockRegisterDto);

        if (result){
            return HttpStatus.OK;
        } else {
            //304
            return HttpStatus.NOT_MODIFIED;
        }
    }

    //Graph Data 조회

    //Comment 남기는 기능

    // 뉴스링크 스르랩 기능 -> nullable


    //삭제
}
