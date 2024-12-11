package com.stocker.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocker.backend.exceptionHandling.BadRequestException;
import com.stocker.backend.exceptionHandling.InternalServerErrorException;
import com.stocker.backend.exceptionHandling.NotAcceptableException;
import com.stocker.backend.model.dto.response.SearchResultDto;
import com.stocker.backend.model_stocks.MemberFavorite;
import com.stocker.backend.model_stocks.PortfolioStocks;
import com.stocker.backend.model_stocks.StockRegisterDto;
import com.stocker.backend.model_stocks.response.ChartDataDto;
import com.stocker.backend.model_stocks.response.ChartDataResponse;
import com.stocker.backend.repository.MemberFavoriteRepository;
import com.stocker.backend.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private static final Logger logger = LogManager.getLogger(PortfolioService.class);
    private final PortfolioRepository portfolioRepository;
    private final MemberFavoriteRepository memberFavoriteRepository;

    @Value("${apiKey.fmp}")
    private String apiKey;
    @Value("${fmp.period}")
    private String period;
    @Value("${fmp.endpoint}")
    private String fmpEndpoint;
    @Value("${fmp.interval}")
    private int fmpInterval;

    public boolean registerFavorite(StockRegisterDto stockRegisterDto, String id) throws MalformedURLException {

        MemberFavorite memberFavorite=  memberFavoriteRepository.findMemberFavoritesByIdAndSymbol(id,stockRegisterDto.getSymbol());
        String symbol = stockRegisterDto.getSymbol();
        // 주식 등록이 성공한 경우
        if (memberFavorite ==null){
            logger.info("Register Member Favorite ID : {}, Symbol : {}", id,symbol);
            MemberFavorite favorite = MemberFavorite.builder()
                    .id(id)
                    .symbol(symbol)
                    .updatedAt(LocalDateTime.now())
                    .build();
            memberFavoriteRepository.save(favorite);
            return true;
        } else {
            logger.info("Favorite is already exist ID : {}, Symbol : {}",id, symbol);
            return false;
        }
    };

    public ResponseEntity<SearchResultDto> searchSymbol(String symbol) throws IOException {

//        URL url = new URL(String.format(fmpEndpoint+"/v3/income-statement/%s?period=%s&limit=100&apikey=%s",symbol,period,apiKey));
        URL url = new URL(String.format(fmpEndpoint+"/v3/key-metrics/%s?apikey=%s",symbol,apiKey));

        //해당 주식 DB 조회 -> FMP의 요금제에 따라서 변경
        PortfolioStocks dbData = portfolioRepository.findFirstBySymbolOrderByAccountDateDesc(symbol);
//        logger.info("DB Data : "+ dbData);

        long daysBetween = fmpInterval;
        if(dbData !=null) {
            daysBetween = ChronoUnit.DAYS.between(dbData.getAccountDate(), LocalDate.now());
        }

        if (daysBetween < fmpInterval){
            logger.info("FMP interval({}) is bigger than dayBetween {}",fmpInterval ,daysBetween);
            SearchResultDto searchResultDto = new SearchResultDto(symbol);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(searchResultDto);
        } else {

            //HttpURLConnection을 사용하여 데이터 읽기
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            //성공
            StringBuilder response = new StringBuilder();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    for (String line; (line = reader.readLine()) != null;) {
                        response.append(line);
                    }
                    // JSON 데이터 출력
//                    logger.info("Raw JSON Response: " + response.toString());

                    // JSON 파싱
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Map<String, Object>> jsonList = objectMapper.readValue(response.toString(), List.class);


                    // 결과 저장
                    List<PortfolioStocks> dataList = new ArrayList<PortfolioStocks>();
                    for (Map<String, Object> entry : jsonList) {
                        //netIncomeRatio
                        PortfolioStocks portfolioStocks =  PortfolioStocks.builder()
                                .accountDate(LocalDate.parse( entry.get("date").toString()))
                                .symbol(entry.get("symbol").toString())
                                .eps(parseFloat(entry.get("netIncomePerShare")))
                                .per(parseFloat(entry.get("peRatio")))
                                .roe(parseFloat(entry.get("roe")))
                                .updatedAt(LocalDateTime.now())
                                .build();
                        dataList.add(portfolioStocks);

    //                    System.out.println("Date: " + entry.get("date"));
    //                    System.out.println("Net Income: " + entry.get("netIncome"));
    //                    System.out.println("EPS: " + entry.get("eps")+'\n');
                portfolioRepository.saveAll(dataList);
                    }
                    SearchResultDto searchResultDto = new SearchResultDto(symbol);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(searchResultDto);
                } catch (Exception e){
                    logger.error(e);
                    throw new BadRequestException("요청하신 Stock Symbol이 잘못됐습니다");
                }
            } else if (responseCode ==401){
                //apiKey 가 잘못된 경우
                logger.error("apiKey is invalid");
                throw new NotAcceptableException("데이터를 가져올 수 없습니다");

            }
            else { // 실패
                logger.error("HTTP Error: " + responseCode);
                throw new InternalServerErrorException("Internal Server Error. Request to Admin");

            }
        }
    }


    //delete favorite
    public boolean deleteSymbol(String id, String symbol){

        int rowsDeleted = memberFavoriteRepository.deleteByIdAndSymbol(id, symbol);
        logger.info("Delete rows num : {}", rowsDeleted);
        return rowsDeleted > 0;
    }


    public List<ChartDataResponse> getFinanceData(String symbol){
        List<ChartDataDto> result = portfolioRepository.findFinancialRatiosBySymbol(symbol);
//        logger.info(result);

        return transformData(result);
    }



    //float 변환 메서드
    private Float parseFloat(Object floatObj) {
        if (floatObj == null) return null;

        try {
            if (floatObj instanceof Float) {
                return (Float) floatObj; // 이미 Float 타입
            } else if (floatObj instanceof Double) {
                return ((Double) floatObj).floatValue(); // Double → Float 변환
            } else if (floatObj instanceof String) {
                return Float.parseFloat((String) floatObj); // String → Float 변환
            }
        } catch (Exception e) {
            System.err.println("Float 변환 실패: " + floatObj);
        }
        return null;
    }

    public List<ChartDataResponse> transformData(List<ChartDataDto> rawData) {
        // 변환할 카테고리 정의
        String[] categories = {"EPS", "PER", "ROE"};

        List<ChartDataResponse> chartDataList = new ArrayList<>();

        for (String category : categories) {
            List<ChartDataResponse.PriceData> prices = new ArrayList<>();

            for (ChartDataDto dto : rawData) {
                Float value = null;

                switch (category) {
                    case "EPS":
                        value = dto.getEps();
                        break;
                    case "PER":
                        value = dto.getPer();
                        break;
                    case "ROE":
                        value = dto.getRoe();
                        break;
                }

                prices.add(new ChartDataResponse.PriceData(
                        dto.getAccountDate().toString(), // LocalDate -> String
                        value
                ));
            }

            chartDataList.add(new ChartDataResponse(category, prices));
        }

        return chartDataList;
    }
}
