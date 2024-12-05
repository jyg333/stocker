package com.stocker.backend.service;

import com.stocker.backend.model_stocks.response.AmountDto;
import com.stocker.backend.model_stocks.response.AmountResponseDto;
import com.stocker.backend.model_stocks.response.TradeResultDto;
import com.stocker.backend.repository.AlTradeRepository;
import com.stocker.backend.repository.AlTradeResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlTradeResultService {

    private final AlTradeResultRepository alTradeResultRepository;
    private final AlTradeRepository alTradeRepository;

    public List<TradeResultDto> getLatestTradeResults(String symbol) {
        return alTradeResultRepository.findLatestTradeResultsBySymbol(symbol);
    }

    //시작가격, 현재가격, 변화율
    public AmountResponseDto getAmount(String symbol){

        AmountDto amountDto = alTradeRepository.findBySymbolInitAndCurAmount(symbol, alId);
    }
}
