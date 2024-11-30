package com.stocker.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocker.backend.exceptionHandling.BadRequestException;
import com.stocker.backend.exceptionHandling.InternalServerErrorException;
import com.stocker.backend.exceptionHandling.NotAcceptableException;
import com.stocker.backend.model.stocks.PortfolioStocks;
import com.stocker.backend.model.stocks.StockRegisterDto;
import com.stocker.backend.repository.PortfolioRepository;
import com.stocker.backend.utils.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private static final Logger logger = LogManager.getLogger(PortfolioService.class);
    private final PortfolioRepository portfolioRepository;
    @Value("${apiKey.fmp}")
    private String apiKey;
    @Value("${fmp.period}")
    private String period;
    @Value("${fmp.endpoint}")
    private String fmpEndpoint;

    public boolean registerFavorite(StockRegisterDto stockRegisterDto) throws MalformedURLException {


        // 주식 등록이 성공한 경우
        if (true){
            return true  ;
        } else {
            return false;

        }

    };

    public ResponseEntity<?> searchSymbol(String symbol) throws IOException {

        URL url = new URL(String.format(fmpEndpoint+"/v3/income-statement/%s?period=%s&limit=100&apikey=%s",symbol,period,apiKey));

        System.out.println("Request URL: " + url);

        StringBuilder response = new StringBuilder();

        // HttpURLConnection을 사용하여 데이터 읽기
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == 200) { // 성공
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    response.append(line);
                }
                // JSON 데이터 출력
//        logger.info("Raw JSON Response: " + response.toString());

                // JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> jsonList = objectMapper.readValue(response.toString(), List.class);

//        // 결과 출력
                List<PortfolioStocks> dataList = new ArrayList<PortfolioStocks>();
                for (Map<String, Object> entry : jsonList) {
                    //netIncomeRatio
                    PortfolioStocks portfolioStocks =  PortfolioStocks.builder()
                            .accountDate(LocalDate.parse( entry.get("date").toString()))
                            .symbol(entry.get("symbol").toString())
                            .eps(parseFloat(entry.get("eps")))
                            .netIncomeRatio(parseFloat(entry.get("netIncomeRatio")))
                            .updatedAt(LocalDateTime.now())
                            .build();
                    dataList.add(portfolioStocks);

                    System.out.println("Date: " + entry.get("date"));
                    System.out.println("Net Income: " + entry.get("netIncome"));
                    System.out.println("EPS: " + entry.get("eps")+'\n');
//            portfolioRepository.saveAll(dataList);
                }

                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Success");
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
}
