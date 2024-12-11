package com.stocker.backend.service;

import com.stocker.backend.controller.AuthController;
import com.stocker.backend.exceptionHandling.ExpectationFailedException;
import com.stocker.backend.exceptionHandling.NotAcceptableException;
import com.stocker.backend.model_stocks.AlTrade;
import com.stocker.backend.model_stocks.AlTypes;
import com.stocker.backend.model_stocks.MemberFavorite;
import com.stocker.backend.model_stocks.request.UpdateAlStatusDto;
import com.stocker.backend.model_stocks.request.UpdateAlTradeDto;
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
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlTradeResultService {

    private final AlTradeResultRepository alTradeResultRepository;
    private final AlTradeRepository alTradeRepository;
    private final MemberFavoriteRepository memberFavoriteRepository;
    private final AlTypesRepository alTypesRepository;

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

        //nullable buyAmount
        Long buyAmount = amountDto.getBuyAmount();
        if (buyAmount == null){
            buyAmount =0L;
        }


        // ratio 계산 (initAmount가 0인 경우를 처리)
        double ratio = amountDto.getInitAmount() > 0
                ? ((double) amountDto.getCurAmount()+ - buyAmount) / amountDto.getInitAmount() * 100
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

    public boolean registerAlTrade(String id, UpdateAlTradeDto updateAlTradeDto){
        try {
            AlTrade alTrade = new AlTrade();
            AlTypes altype = alTypesRepository.findByAlType(updateAlTradeDto.getAlType());

            LocalDateTime endAt = LocalDateTime.parse(updateAlTradeDto.getEndAt());

            alTrade.setSymbol(updateAlTradeDto.getSymbol());
            alTrade.setId(id);
            alTrade.setAlId(altype);
            alTrade.setInitAmount(updateAlTradeDto.getInitAmount());
            alTrade.setEndAt(endAt);
            alTrade.setUpperLimit(updateAlTradeDto.getUpperLimit());
            alTrade.setLowerLimit(updateAlTradeDto.getLowerLimit());
            alTrade.setStartAt(LocalDateTime.now());
            alTrade.setActivation(true);
            alTrade.setUpdatedAt(LocalDateTime.now());
//        alTrade.setAlId();
            MemberFavorite memberFavorite = memberFavoriteRepository.findByIdAndSymbol(id, updateAlTradeDto.getSymbol());
            memberFavorite.setAlStatus(true);
            memberFavoriteRepository.save(memberFavorite);
            alTradeRepository.save(alTrade);
        }catch (Exception e){
            logger.error("Save al_trade Exception  : {}",e);
            throw new NotAcceptableException("Fail register Al Trade");
        }

        return true;
    }

    public List<String> getAllTypes(){
        List<AlTypes> alTypesList = alTypesRepository.findAll();

        // Stream API를 사용하여 al_type 값만 추출하여 List<String>으로 반환
        return alTypesList.stream()
                .map(AlTypes::getAlType) // AlTypes 객체의 al_type 필드 값 추출
                .collect(Collectors.toList()); // List<String>으로 변환
    }
}
