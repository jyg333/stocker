package com.stocker.backend.service;

import com.stocker.backend.controller.AuthController;
import com.stocker.backend.exceptionHandling.ExpectationFailedException;
import com.stocker.backend.model_stocks.AlTrade;
import com.stocker.backend.model_stocks.AlTypes;
import com.stocker.backend.model_stocks.MemberFavorite;
import com.stocker.backend.model_stocks.request.UpdateAlStatusDto;
import com.stocker.backend.model_stocks.response.AmountDto;
import com.stocker.backend.model_stocks.response.AmountResponseDto;
import com.stocker.backend.model_stocks.response.TradeResultDto;
import com.stocker.backend.repository.AlTradeRepository;
import com.stocker.backend.repository.AlTradeResultRepository;
import com.stocker.backend.repository.AlTypesRepository;
import com.stocker.backend.repository.MemberFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlTradeResultService {

    private final AlTradeResultRepository alTradeResultRepository;
    private final AlTradeRepository alTradeRepository;
    private final MemberFavoriteRepository memberFavoriteRepository;

    private static final Logger logger = LogManager.getLogger(AlTradeResultService.class);

    public List<TradeResultDto> getLatestTradeResults(String symbol) {
        return alTradeResultRepository.findLatestTradeResultsBySymbol(symbol);
    }

    //시작가격, 현재가격, 변화율
    public AmountResponseDto getAmount(String symbol, String id){

        AlTrade alTrade = alTradeRepository.findByIdAndSymbol(id, symbol);

        logger.info(alTrade);
        if (alTrade == null){
            logger.info("There is not id : {}, symbol: {} to getAmount()");
            throw new ExpectationFailedException("There is not data to trade");
        }
        Byte alId = alTrade.getAlId().getAlId();


        AmountDto amountDto = alTradeRepository.findBySymbolInitAndCurAmount(symbol, alId);

        AmountResponseDto amountResponseDto = new AmountResponseDto();
        amountResponseDto.setInitAmount(amountDto.getInitAmount());
        amountResponseDto.setCurAmount(amountDto.getCurAmount());

        // ratio 계산 (initAmount가 0인 경우를 처리)
        double ratio = amountDto.getInitAmount() > 0
                ? ((double) amountDto.getCurAmount() - amountDto.getInitAmount()) / amountDto.getInitAmount() * 100
                : 0.0;
        amountResponseDto.setRatio(ratio);


        return amountResponseDto;
    }

    //update member_favorite.al_status
    public boolean updateFavorite(String id, UpdateAlStatusDto updateAlStatusDto){
        String symbol = updateAlStatusDto.getSymbol();
        boolean status = updateAlStatusDto.getAl_status();
        MemberFavorite memberFavorite = memberFavoriteRepository.findByIdAndSymbol(id, symbol);
        logger.info("Update Favorite Id : {}, Symbol : {}, status : {}", id, symbol,status);
        if (memberFavorite == null ){
            throw new ExpectationFailedException("Algorithm trade register fail");
        }
        memberFavorite.setAlStatus(status);
        memberFavoriteRepository.save(memberFavorite);

        return true;


    }
}
