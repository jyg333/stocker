package com.stocker.backend.service;

import com.stocker.backend.exceptionHandling.*;
import com.stocker.backend.model.dto.*;
import com.stocker.backend.model.dto.request.UpdateDetailDto;
import com.stocker.backend.model.dto.request.UpdatePasswordDto;
import com.stocker.backend.model.dto.request.UpdateSimpleDto;
import com.stocker.backend.model.dto.response.MemberListDto;
import com.stocker.backend.model.dto.response.MemberAllListDto;
import com.stocker.backend.model.entity.*;
import com.stocker.backend.repository.*;
import com.stocker.backend.utils.Checker;
import com.stocker.backend.utils.JwtProvider;
import com.stocker.backend.utils.RoleSaver;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private static final Logger logger = LogManager.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final RoleInfoRepository roleInfoRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordLogRepository passwordLogRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;

    private final InspectionRepository inspectionRepository;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private Checker checker;
    @Autowired
    private RoleSaver roleSaver;

    // 사용자 목록 조회, 권한("ADMIN") 확인 추가, password를 제회한 결괏값 조회
    public List<MemberListDto> findAllMembers(String token, String roleName){
        //Spring Security Filter에서 권환 확인 완료
        Claims payload = jwtProvider.parseClaims(token);
        Object rolesObject = payload.get("roles");

        // ADMIN 권한 확인
        if (!checker.checkRoleLevelAll(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }

        List<Object[]> results = memberRepository.findMemberListDto(roleName);
        Map<String, MemberListDto> memberMap = new HashMap<>();

        for (Object[] result : results) {
            String id = (String) result[0];
            String name = (String) result[1];
            String rolesString  = (String) result[2];

            List<String> roles = Arrays.asList(rolesString.split(","));

            // If member already exists in the map, add the role
            if (memberMap.containsKey(id)) {
                memberMap.get(id).getRoles().addAll(roles);
            } else {
                // Create new MemberListDto and add the first role
                MemberListDto dto = new MemberListDto(id, name, new ArrayList<>(roles));
                memberMap.put(id, dto);
            }
        }

        return new ArrayList<>(memberMap.values());
    }


    // 본인 정보 조회
    public MemberSimpleInfoDto getMyInfo(String token){

        Claims payload = jwtProvider.parseClaims(token);
        String id = (String) payload.get("userId");
        Object rolesObject = payload.get("roles");

        //USER or ADMIN 권한 확인
        if (!checker.checkRoleLevelTwoAndThree(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }

        logger.info("My Info Claims : {}",payload);
        MemberSimpleDto memberSimpleDto = memberRepository.findMemberDetailsById(id);

        if (memberSimpleDto !=null) {

            List<String> roles = roleInfoRepository.findAllWithMemberRolesByMemberId(id);

            MemberSimpleInfoDto memberSimpleInfoDto = MemberSimpleInfoDto.builder()
                    .id(memberSimpleDto.getId())
                    .name(memberSimpleDto.getName())
                    .passwordUpdatedDate(memberSimpleDto.getPasswordUpdatedDate())
                    .joinIp(memberSimpleDto.getJoinIp())
                    .joinDate(memberSimpleDto.getJoinDate())
                    .roles(roles)
                    .build();


            return memberSimpleInfoDto;
        }else{
            throw new  ResourceNotFoundException(String.format("ID : %S Does not exist",id));
        }
    }
    // 사용자 조회, 권한 : ADMIN
    public MemberDetailInfo getMemberInfo(String token,String id){

        Claims payload = jwtProvider.parseClaims(token);
        Object rolesObject = payload.get("roles");

        // ADMIN 권한 확인
        if (!checker.checkRoleLevelTwo(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }


        MemberDetailDto detail = memberRepository.findDetailById(id);
        if (detail == null) {
            throw new ResourceNotFoundException(String.format("ID : %s does not exist", id));
        }

        if (detail.getId().equals(id)){
            List<String> roles = roleInfoRepository.findAllWithMemberRolesByMemberId(id);
            //todo : null 인경우 예외처리 추가하기
            MemberDetailInfo memberDetailInfo = MemberDetailInfo.builder()
                    .idx(detail.getIdx())
                    .id(detail.getId())
                    .name(detail.getName())
                    .activation(detail.getActivation())
                    .passwordUpdatedDt(detail.getPasswordUpdatedDt())
                    .failCount(detail.getFailCount())
                    .failDt(detail.getFailDt())
                    .joinIp(detail.getJoinIp())
                    .joinDt(detail.getJoinDt())
                    .createdDt(detail.getCreatedDt())
                    .createdBy(detail.getCreatedBy())
                    .updatedDt(detail.getUpdatedDt())
                    .updatedBy(detail.getUpdatedBy())
                    .roles(roles)
                    .build();
            logger.info("Get MyInfo Successful || ID : {}",id);
            return memberDetailInfo;
        }
        else {
            logger.error("Get MyInfo Fail || ID : {}",id);
            throw new  ResourceNotFoundException(String.format("ID : %S Does not exist",id));
        }
    }

    // 특정 사용자 정보 변경, todo : 사용자 권한 검증, 입력값 검사
    public void updateMember(String token, UpdateDetailDto updateDetailDto, HttpServletRequest request){

        Claims payload = jwtProvider.parseClaims(token);
        String id = updateDetailDto.getId();
        Object rolesObject = payload.get("roles");
        // 권한검증 -> 403
        if (!checker.checkRoleLevelTwo(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }
        // 입력값 검증 -> 422
        checker.validateUpdateDetailDto(updateDetailDto);

        LocalDateTime now = LocalDateTime.now();

        Inspection inspection = new Inspection();
        inspection.setSavedAt(now);


        Member beforeMember = memberRepository.findById(id);
        LoginLog beforeLog = loginLogRepository.findLoginLogByMemberId(id);


        if (beforeMember!= null){
            Integer idx = beforeMember.getIdx();
            //id, password는 변경하지 않음
            beforeMember.setActivation(updateDetailDto.isActivation());
            if (updateDetailDto.getName() !=null){
                beforeMember.setName(updateDetailDto.getName());
            }


            beforeLog.setFailCount(updateDetailDto.getFailCount());
            beforeLog.setUpdatedBy(id);
            beforeLog.setUpdatedDt(LocalDateTime.now());
            memberRepository.save(beforeMember);

            //roles update
//            List<String> roles = memberRoleRepository.findRoleNameByIdx(idx);
//            logger.info("Target ID : {} || Roles info before : {}, After : {}", id, roles, updateDetailDto.getRoles());

//            Set<String> rolesSet = new HashSet<>(roles);
//            Set<String> updatedRolesSet = new HashSet<>(updateDetailDto.getRoles());
//            if(!rolesSet.equals(updatedRolesSet)){
//                List<Integer> rolesIds = roleInfoRepository.findRoleIdsByRoleNames(updateDetailDto.getRoles());
//                updateRoles(rolesIds, idx);
//            }

            inspection.setContent(String.format("사용자 정보 변경 || 관리자 ID : %s ||  대상 ID : %s || Activation : %s || Fail-Count : %s ",payload.get("userId"),updateDetailDto.getId(), updateDetailDto.isActivation(), updateDetailDto.getFailCount()));
            inspectionRepository.save(inspection);
        }else{
            throw new ResourceNotFoundException(String.format("ID : %s Does not exist",id));
        }
    }
    //본인 정보 변경
    public void updateMyInfo(String token, UpdateSimpleDto updateSimpleDto){

        Claims payload = jwtProvider.parseClaims(token);
        String id = payload.get("userId").toString();
        Object rolesObject = payload.get("roles");

        // 권한 검증 0 -> 403
        if (!checker.checkRoleLevelTwoAndThree(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }
        //입력값 검증 -> 422
//        checker.validateUpdateSimpleDto(updateSimpleDto);

        Member beforeMember = memberRepository.findById(id);
        LoginLog beforeLog = loginLogRepository.findLoginLogByMemberId(updateSimpleDto.getId());


        // Token의 id 가 null 이 아닌경우 && updateUser와 JWT 의 발급자가 같은경우
//        if (beforeMember !=null && id.equals(updateSimpleDto.getUpdateUser())){
        if (beforeMember !=null && id.equals(updateSimpleDto.getId())){
            //id, password는 변경하지 않음
            beforeMember.setName(updateSimpleDto.getName());
            beforeLog.setUpdatedBy(updateSimpleDto.getId());
            beforeLog.setUpdatedDt(LocalDateTime.now());

            memberRepository.save(beforeMember);
            loginLogRepository.save(beforeLog);
        }else {
            throw new ResourceNotFoundException(String.format("ID : %s Does not exist",id));
        }
    }

    //사용자 비밀번호 변경,todo : 권한 확인, 입력값 검사, password encoding
    public void updatePassword(String token, UpdatePasswordDto updatePasswordDto){
        //todo : 이전의 암호 기록하기, 입력값 검증
        Claims payload = jwtProvider.parseClaims(token);
        String id = payload.get("userId").toString();
        Object rolesObject = payload.get("roles");

        // 권한 검증 0 -> 403
        if (!checker.checkRoleLevelTwoAndThree(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }

        Member member = memberRepository.findById(id);
        // 현재 비밀번호 확인 -> 400
        if(member == null ){
            logger.error("Update password Fail || Member ID does not exist || ID : {}",id);
            throw new ResourceNotFoundException(String.format("ID : %S Does not exist",id));
        }
        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), member.getPassword())) {
            logger.error("Update password Fail || Password Does not match || ID : {}",id);
            throw new BadRequestException("Current password is incorrect");
        }

        if (updatePasswordDto.getNewPassword().equals(updatePasswordDto.getValidPassword())) {
            // 새 비밀번호 암호화
            String encodedNewPassword = passwordEncoder.encode(updatePasswordDto.getNewPassword());
            member.setPassword(encodedNewPassword);
            memberRepository.save(member);
            //update password log table
            PasswordLog passwordLog = passwordLogRepository.findByMemberId(id);
            passwordLog.setLastPassword(updatePasswordDto.getCurrentPassword()); // 필요시 암호화 추가
            passwordLog.setUpdatedDt(LocalDateTime.now());
            passwordLogRepository.save(passwordLog);
            Inspection inspection = Inspection.builder().savedAt(LocalDateTime.now())
                    .content(String.format("비밀번호 재설정 || ID : %s", id))
                    .build();
            inspectionRepository.save(inspection);
        }else{
            throw new UnprocessableEntityException("New Password is not same with Valid Password.");
        }
    }

    //사용자 비밀번호 초기화, Todo : 관리자 권한 확인
    public PasswordResetResponseDto resetPassword(String token, PasswordResetDto passwordResetDto, HttpServletRequest request){
        
        //todo : tempPassword 관리방법 설정 + 만료 방법 설정
        Claims payload = jwtProvider.parseClaims(token);
        Object rolesObject = payload.get("roles");
        //권한 확인
        if (!checker.checkRoleLevelTwo(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }
        Member member = memberRepository.findById(passwordResetDto.getId());
        //DB값 존재확인
        if (member !=null) {
            boolean useLetters = true;
            boolean useNumbers = true;
            String randomStr = RandomStringUtils.random(10, useLetters, useNumbers);

            String encodedNewPassword = passwordEncoder.encode(randomStr);
            member.setPassword(encodedNewPassword);

            memberRepository.save(member);
            PasswordResetResponseDto response = new PasswordResetResponseDto();
            response.setTempPassword(randomStr);

            Inspection inspection = Inspection.builder().savedAt(LocalDateTime.now())
                    .content(String.format("비밀번호 초기화 요청 ID : %s || 초기화 대상 ID : %s",payload.get("userId"),passwordResetDto.getId())).build();
            inspectionRepository.save(inspection);

            return response;
        }
        else {
            throw new ResourceNotFoundException(String.format("ID : %s Does not exist",passwordResetDto.getId()));
        }
    }
    //사용자 삭제, todo : 권한 검증, 대상조회, delete 명령어 확인인
    public void deleteService(String token, String id, HttpServletRequest request){

        Claims payload = jwtProvider.parseClaims(token);
        Object rolesObject = payload.get("roles");
        // 권한검증 -> 403
        if (!checker.checkRoleLevelTwo(rolesObject)){
            throw new ForbiddenException("You do not have permission to access this resource.");
        }

        Member member = memberRepository.findById(id);
        if(member != null) {
            memberRoleRepository.deleteAllByIdx(member.getIdx());
            passwordLogRepository.deleteByMemberId(member.getId());
            loginLogRepository.deleteLoginLogById(member.getId());
            refreshTokenRepository.deleteById(member.getId());
            // Member 엔티티를 삭제합니다. CascadeType.ALL 및 orphanRemoval = true 덕분에 관련된 자식 엔티티도 삭제됩니다.
            memberRepository.delete(member);

            Inspection inspection = Inspection.builder().savedAt(LocalDateTime.now())
                    .content(String.format("Delete Account || Request ID : %s || Target ID : %s",payload.get("userId"),id)).build();
            inspectionRepository.save(inspection);
        }
        else{
            throw new ResourceNotFoundException("Member with ID " + id + " not found");
        }

    }

    public List<MemberAllListDto> findAllMembers(String token, Integer limit, Integer offset){
        //Spring Security Filter에서 권환 확인 완료
        Claims payload = jwtProvider.parseClaims(token);
        Object rolesObject = payload.get("roles");

        // ADMIN 권한 확인
        if (!checker.checkRoleLevelTwo(rolesObject)){
            logger.error("Find All Member Fail, Caused by Role permission denied");
            throw new ForbiddenException("You do not have permission to access this resource");
        }

        List<Object[]> results = memberRepository.findMemberListDto(limit, offset);
        List<MemberAllListDto> memberList = new ArrayList<>();

        for (Object[] result : results) {
            String id = (String) result[0];
            String position = (String) result[1];
            Boolean activation = (Boolean) result[2];
            Integer failCount = (Integer) result[3];
            String rolesString = (String) result[4];
            List<String> roles = Arrays.asList(rolesString.split(",\\s*"));

            MemberAllListDto memberAllListDto = MemberAllListDto.builder()
                    .id(id)
                    .position(position)
                    .activation(activation)
                    .failCount(failCount)
                    .roles(roles)
                    .build();

            memberList.add(memberAllListDto);
        }

    return memberList;
    }
    //find Member version2
    public List<MemberTotalDto> findMembersV2(String token) {
        //Spring Security Filter에서 권환 확인 완료
        Claims payload = jwtProvider.parseClaims(token);
        Object rolesObject = payload.get("roles");

        // ADMIN 권한 확인
        if (!checker.checkRoleLevelTwo(rolesObject)) {
            throw new ForbiddenException("You do not have permission to access this resource.");
        }
        List<Object[]> results = memberRepository.findMemberList();
        List<MemberTotalDto> memberList = new ArrayList<>();

        for (Object[] row : results) {

            MemberTotalDto memberDetailSOC = MemberTotalDto.builder()
                    .id(row[0] != null ? (String) row[0] : null)
                    .name(row[1] != null ? (String) row[1] : null)
                    .activation(row[2] != null ? (Boolean) row[2] : null)
                    .failCount(row[3] != null ? (Integer) row[3] : 0) // 기본값으로 0 설정
                    .fail_dt(convertToLocalDateTime(row[4]))
                    .join_dt(convertToLocalDateTime(row[5]))
                    .join_ip(row[6] != null ? (String) row[6] : null)
                    .create_dt(convertToLocalDateTime(row[7]))
                    .created_by(row[8] != null ? (String) row[8] : null)
                    .updated_dt(convertToLocalDateTime(row[9]))
                    .updated_by(row[10] != null ? (String) row[10] : null)
                    .roles(row[11] != null ? Arrays.asList(((String) row[11]).split(", ")) : null)
                    .build();
            memberList.add(memberDetailSOC);
        }
        return memberList;
    }


        // Get Member Count
    public Long getCount(){
        Long count = memberRepository.count();
        return count;
    }

    private LocalDateTime convertToLocalDateTime(Object timestamp) {
        if (timestamp instanceof Timestamp) {
            return ((Timestamp) timestamp).toLocalDateTime();
        }
        return null;
    }

    protected void updateRoles(List<Integer> newRoles, Integer idx){
        memberRoleRepository.deleteAllByIdx(idx);
        roleSaver.saveRole(roleInfoRepository, memberRoleRepository, newRoles, idx);

    }

}