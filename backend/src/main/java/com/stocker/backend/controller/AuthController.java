package com.stocker.backend.controller;


import com.stocker.backend.exceptionHandling.*;
import com.stocker.backend.model.TokenDTO;
import com.stocker.backend.model.dto.*;
import com.stocker.backend.model.dto.request.KeyGenDto;
import com.stocker.backend.model.dto.request.MemberLogoutDto;
import com.stocker.backend.model.dto.request.MemberRegisterDto;
import com.stocker.backend.model.entity.Inspection;
import com.stocker.backend.repository.InspectionRepository;
import com.stocker.backend.service.AuthService;
import com.stocker.backend.utils.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

//Todo : Exception Handler 추가하기!!
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final InspectionRepository inspectionRepository;
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    //logout, refreshToken을 삭제하여 null로 만든다. Client 단에서 AccessToken과 RefreshToken을 삭제함
    @PostMapping("/logout")
    public void logout(@Valid @RequestBody MemberLogoutDto memberLogoutDto, Errors errors, HttpServletRequest request){

        Inspection inspection = Inspection.builder()
                .savedAt(LocalDateTime.now())
                .content(String.format("Logout || ID : %s ", memberLogoutDto.getId()))
                .build();
        inspectionRepository.save(inspection);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authorize_before(@Valid @RequestBody  MemberLoginDto memberLoginDto, Errors errors, HttpServletRequest request){

        if(errors.hasErrors()){
            throw new UnprocessableEntityException("Request Body validation error");
        }
        //todo : otp 사용 여부 확인 추가 2024-07-25

            TokenDTO jwt = authService.loginRequest(memberLoginDto,request);
            authService.saveRefreshToken(jwt);

            // HTTP 헤더에 JWT 포함
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.BEARER_PREFIX + jwt.getAccessToken());

            // JWT와 헤더를 포함한 응답 반환
            return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);

    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<RegisterDto> registerTest(@Valid @RequestBody MemberRegisterDto registerDto, Errors errors, HttpServletRequest request) {

        if(errors.hasErrors()){
            throw new UnprocessableEntityException("Request Body validation error");
        }
        String tempPassword = authService.registerMember(registerDto,request);

        RegisterDto responseDto = new RegisterDto();
        responseDto.setTempPassword(tempPassword);
//        logger.info("Registration Successful || ID : {}", registerDto.getId());
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    // token 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenDTO> reissue(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto,Errors errors) {

        if(errors.hasErrors()){
            throw new UnprocessableEntityException("Request Body validation error");
        }
        return ResponseEntity.ok(authService.reissue(refreshTokenRequestDto));
    }

    // API key 발급
//    @PostMapping("/api-key")
//    public ResponseEntity<APIKeyDto> genKey(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody KeyGenDto keyGenDto, Errors errors){
//
//        if(errors.hasErrors()){
//            throw new UnprocessableEntityException("Request Body validation error");
//        }
//        String token = authorizationHeader.replace("Bearer ","");
//        logger.info("API Key Gen Request Id : {}",keyGenDto.getId());
//
//        APIKeyDto apiKeyDto =authService.generateKey(token, keyGenDto);
//        return new ResponseEntity<>(apiKeyDto, HttpStatus.OK);
//    }

}

