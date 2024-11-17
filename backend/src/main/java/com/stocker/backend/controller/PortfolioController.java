package com.stocker.backend.controller;

import com.stocker.backend.model.dto.request.UpdateDetailDto;
import com.stocker.backend.model.stocks.StockRegisterDto;
import com.stocker.backend.service.PortfolioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    //Search Bar를 통한 주식 검색
    @PostMapping("/search")
    public EntityResponse<String> searchStock(){
        return null;
    }

    // 즐겨찾기 등록
    @PostMapping("/add-favorite")
    public HttpStatus registerStock(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody StockRegisterDto stockRegisterDto, Errors errors, HttpServletRequest request){

        boolean result = portfolioService.registerFavorite(stockRegisterDto);

        if (result){
            return HttpStatus.OK;
        } else {
            //304
            return HttpStatus.NOT_MODIFIED;
        }
    }

    //Graph Data 조회

    //삭제
}
