package com.stocker.backend.service;

import com.stocker.backend.model.stocks.StockRegisterDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PortfolioService {

    public boolean registerFavorite(StockRegisterDto stockRegisterDto){


        // 주식 등록이 성공한 경우
        if (true){
            return true  ;
        } else {
            return false;

        }

    };
}
