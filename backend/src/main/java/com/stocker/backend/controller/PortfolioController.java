package com.stocker.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    //Search Bar를 통한 주식 검색
    @PostMapping("/search")
    public EntityResponse<String> searchStock(){
        return null;
    }
    // 즐겨찾기 등록

    //삭제
}
