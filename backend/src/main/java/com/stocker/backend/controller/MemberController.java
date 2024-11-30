package com.stocker.backend.controller;
/*사용자 조회, 변경, 권한 관리*/

import com.stocker.backend.exceptionHandling.BadRequestException;
import com.stocker.backend.exceptionHandling.InternalServerErrorException;
import com.stocker.backend.exceptionHandling.UnprocessableEntityException;
import com.stocker.backend.model.dto.*;
import com.stocker.backend.model.dto.request.UpdateDetailDto;
import com.stocker.backend.model.dto.request.UpdatePasswordDto;
import com.stocker.backend.model.dto.request.UpdateSimpleDto;
import com.stocker.backend.model.dto.response.MemberAllListDto;
import com.stocker.backend.service.MemberService;
import com.stocker.backend.utils.InetAddressValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private static final Logger logger = LogManager.getLogger(MemberController.class);

    @Autowired
    private InetAddressValidator inetAddressValidator;

    // pagination
    @GetMapping("/member-list")
    public List<MemberAllListDto> getMemberList(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                   @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        String token = authorizationHeader.replace("Bearer ", "");
        return memberService.findAllMembers(token,limit, offset);
    }

    @GetMapping("/member-count")
    public Long getMemberCount(@RequestHeader("Authorization") String authorizationHeader){
        Long count = memberService.getCount();
        return count;
    }


    // 본인 정보 조회 , todo : jwt를 기반으로 user id 조회회
    @GetMapping("/me")
    public MemberSimpleInfoDto getMyInfo(@RequestHeader("Authorization") String authorizationHeader){

        String token = authorizationHeader.replace("Bearer ", "");
        MemberSimpleInfoDto myInfo = memberService.getMyInfo(token);
        return myInfo;
    }


    //특정 사용자 조회, admin 권한이 있어야 가능하도록 설정
    @GetMapping("/{id}")
    public MemberDetailInfo getMember(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) throws AccessDeniedException {

        String token = authorizationHeader.replace("Bearer ", "");

        return memberService.getMemberInfo(token, id);
    }


    // 사용자 정보 변경, todo : 사용자 권한 검증, 입력값 검사
    @PutMapping("/update/{id}")
    public HttpStatus putMember(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody UpdateDetailDto updateDetailDto, Errors errors, HttpServletRequest request) throws Exception {

        if (errors.hasErrors()) {
            throw new UnprocessableEntityException("Request Body validation error.");
        }
        boolean inet = inetAddressValidator.isValidInetAddress(updateDetailDto.getIpAddress());
        if (!inet){
            throw new BadRequestException("Invalid IP Address data type");
        }

        String token = authorizationHeader.replace("Bearer ", "");
        try{

            memberService.updateMember(token, updateDetailDto, request);
            return HttpStatus.OK;
        } catch (Exception e ){
            throw new InternalServerErrorException("Internal Server Error : "+e.getMessage());
        }
    }
    //Todo : 사용자 승인 with ROLE_ADMIN Authentication

    // 본인 정보 변경
    @PutMapping("/update/me")
    public HttpStatus putMyInfo(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody UpdateSimpleDto updateSimpleDto, Errors errors) throws Exception {

        if (errors.hasErrors()) {
            throw new UnprocessableEntityException("Request Body validation error.");
        }
        String token = authorizationHeader.replace("Bearer ","");
            memberService.updateMyInfo(token, updateSimpleDto);
            return HttpStatus.OK;
    }

    //사용자 비밀번호 변경,todo : 권한 확인, 입력값 검사
    @PutMapping("/update/member-pw")
    public ResponseEntity<Void> putPassword(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody UpdatePasswordDto updatePasswordDto, Errors errors){
        if (errors.hasErrors()) {
            throw new UnprocessableEntityException("Request Body validation error.");
        }
        String token = authorizationHeader.replace("Bearer ","");
        memberService.updatePassword(token, updatePasswordDto);
        return null;
    }


    //6-2. 사용자 비밀번호 초기화, Todo : 관리자 권한 확인
    @PutMapping("/reset-password")
    public ResponseEntity<PasswordResetResponseDto> resetPassword(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody PasswordResetDto passwordResetDto, Errors errors, HttpServletRequest request) {
        if (errors.hasErrors()) {
            throw new UnprocessableEntityException("Request Body validation error.");
        }
        String token = authorizationHeader.replace("Bearer ","");
        PasswordResetResponseDto response = memberService.resetPassword(token, passwordResetDto, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //6. 사용자 삭제, todo : 권한 검증, 대상조회, delete 명령어 확인인
    @DeleteMapping("/{id}")
    public void deleteMember(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id,@Valid @RequestBody CommandDto command, Errors errors, HttpServletRequest request){
        if (errors.hasErrors()) {
            throw new UnprocessableEntityException("Request Body validation error.");
        }
        String token = authorizationHeader.replace("Bearer ","");

        if (command.getCommand().equals("DELETE")){
            memberService.deleteService(token, id,request);
        }else {
            throw new BadRequestException("Delete command does not match");
        }
    }

}
