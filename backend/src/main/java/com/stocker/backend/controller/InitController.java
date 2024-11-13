package com.stocker.backend.controller;


import com.stocker.backend.model.InitResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Info : api 테스트를 위한 컨트롤러 이후 불필요한 경우 삭제

@RestController
@RequestMapping("/api")
public class InitController {

    @GetMapping("/init")
    public InitResponse getInit(){

        return new InitResponse("init",200);
    }

}
