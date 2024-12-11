package com.stocker.backend.utils;

import com.stocker.backend.exceptionHandling.UnprocessableEntityException;
import com.stocker.backend.model.dto.request.UpdateDetailDto;
import com.stocker.backend.model.dto.request.UpdateSimpleDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Checker {

    //Roles에 ADMIN 이 있는지 확인
    public boolean checkRoleLevelThree(Object rolesObject){

        // Cast the rolesObject to List<String>
        List<String> roles;
        if (rolesObject instanceof List) {
            roles = ((List<?>) rolesObject).stream()
                    .map(Object::toString) // Convert each object to String
                    .collect(Collectors.toList());
        } else {
            roles = new ArrayList<>();
        }
        
        if (roles.contains("ROLE_USER") || roles.contains("ROLE_ADMIN") || roles.contains("ROLE_OBSERVER")){
            //todo : null 인경우 예외처리 추가하기
            return true;
        }
        else{
            return false;
        }
    }

    //Roles에 ADMIN 이 있는지 확인
    public boolean checkRoleLevelTwo(Object rolesObject){

        // Cast the rolesObject to List<String>
        List<String> roles;
        if (rolesObject instanceof List) {
            roles = ((List<?>) rolesObject).stream()
                    .map(Object::toString) // Convert each object to String
                    .collect(Collectors.toList());
        } else {
            roles = new ArrayList<>();
        }
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_OBSERVER")){
            //todo : null 인경우 예외처리 추가하기
            return true;
        }
        else{
            return false;
        }
    }

    //USER, ADMIN 둘중 하나가 있는지 점검
    public boolean checkRoleLevelTwoAndThree(Object rolesObject){
        List<String> roles;
            if (rolesObject instanceof List) {
            roles = ((List<?>) rolesObject).stream()
                    .map(Object::toString) // Convert each object to String
                    .collect(Collectors.toList());
        } else {
            roles = new ArrayList<>();
        }
            if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_USER") || roles.contains("ROLE_OBSERVER")){
            return true;
        }
            else{
            return false;
        }
    }
    public boolean checkRoleLevelAll(Object rolesObject){
        List<String> roles;
        if (rolesObject instanceof List) {
            roles = ((List<?>) rolesObject).stream()
                    .map(Object::toString) // Convert each object to String
                    .collect(Collectors.toList());
        } else {
            roles = new ArrayList<>();
        }
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_USER")  || roles.contains("ROLE_OBSERVER")){
            return true;
        }
        else{
            return false;
        }
    }
    public void validateUpdateSimpleDto(UpdateSimpleDto updateSimpleDto) {
        if (updateSimpleDto == null) {
            throw new UnprocessableEntityException("UpdateSimpleDto cannot be null");
        }

        if (updateSimpleDto.getId() == null || updateSimpleDto.getId().isEmpty()) {
            throw new UnprocessableEntityException("ID cannot be null or empty");
        }


    }


    public void validateUpdateDetailDto(UpdateDetailDto updateDetailDto) {
        if (updateDetailDto == null) {
            throw new UnprocessableEntityException("UpdateDetailDto cannot be null");
        }

        // ID 검증
        if (updateDetailDto.getId() == null || updateDetailDto.getId().isEmpty()) {
            throw new UnprocessableEntityException("ID cannot be null or empty");
        }

        // Activation 검증: boolean 타입이므로 별도의 검증 필요 없음

        // FailCount 검증
        if (updateDetailDto.getFailCount() != null && updateDetailDto.getFailCount() < 0) {
            throw new UnprocessableEntityException("FailCount cannot be negative");
        }


        // Roles 검증
//        if (updateDetailDto.getRoles() == null || updateDetailDto.getRoles().isEmpty()) {
//            throw new UnprocessableEntityException("Roles cannot be null or empty");
//        }
//        for (String role : updateDetailDto.getRoles()) {
//            if (role == null || role.isEmpty()) {
//                throw new UnprocessableEntityException("Each role in the roles list must be non-null and non-empty");
//            }
//        }

        // UpdateUser 검증
//        if (updateDetailDto.getUpdateUser() == null || updateDetailDto.getUpdateUser().isEmpty()) {
//            throw new UnprocessableEntityException("UpdateUser cannot be null or empty");
//        }
    }


}
