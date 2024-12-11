package com.stocker.backend.service;

import com.stocker.backend.exceptionHandling.*;
import com.stocker.backend.model.TokenDTO;
import com.stocker.backend.model.dto.*;
import com.stocker.backend.model.dto.request.MemberRegisterDto;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MemberRepository memberRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordLogRepository passwordLogRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleInfoRepository roleInfoRepository;

    private final InspectionRepository inspectionRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


//    private final DataHandler dataService;

    @Autowired
    private Checker checker;
    @Autowired
    private RoleSaver roleSaver;

    @Autowired
    private CustomDetailsService customDetailsService;


    // Request Token 검증 후 Access Token 발급
    @Transactional
    public TokenDTO reissue(RefreshTokenRequestDto refreshTokenRequestDto) {

        RefreshToken refreshToken = refreshTokenRepository.findOneById(refreshTokenRequestDto.getId());
        Member reissueMember = memberRepository.findById(refreshTokenRequestDto.getId());
//        logger.info("reissueMember : {}",reissueMember);
        if (reissueMember ==null){
            throw new UnprocessableEntityException("Invalid ID");
        }
        Integer idx = reissueMember.getIdx();
//        logger.info("Reissue Request Idx : {}",idx);

        CreateTokenDto createTokenDto = new CreateTokenDto();
        createTokenDto.setMemberIdx(idx);
        createTokenDto.setName(reissueMember.getName());

        UserDetails userDetails = customDetailsService.loadUserByUsername(reissueMember.getId());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), null, userDetails.getAuthorities()
        );

        if (refreshToken == null){
            throw new RuntimeException("해당 사용자의 RefreshToken은 존재하지 않습니다");
        }else {
            // Refresh Token 검증
            // 요청한 토큰과 DB 토큰이 같은지 검증 -> 정상 동작 테스트완료

            if (!jwtProvider.validateToken(refreshTokenRequestDto.getToken())) {
                throw new UnauthorizedException("Refresh Token is expired.");
            }
            if (refreshTokenRequestDto.getToken().equals(refreshToken.getToken())) {

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                TokenDTO tokenDto = null;
//                logger.info("authentication : {}",authentication);
                createTokenDto.setAuthentication(authentication);
                tokenDto = jwtProvider.createToken(createTokenDto);
                saveRefreshToken(tokenDto);

                return tokenDto;
            } else {
                throw new UnprocessableEntityException("Invalid Token Information.");
            }
        }
    }
    // Info : 로그인 시 Refresh Token을 Database에 저장하는 기능
    @Transactional
    public void saveRefreshToken(TokenDTO tokenDTO){
        //Todo : id, password 로그인 시 token을 parsing 하여, id, expiration_date 등을 저장
        Claims claims = jwtProvider.parseClaims(tokenDTO.getAccessToken());
        LocalDateTime expiration = LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
//        logger.info("Expiration Date : {}", claims.getExpiration());
//        logger.info("Converted Expiration Date  : {}", expiration);

        String id = (String) claims.get("userId");
        // Database에 조회하여 기존에 id가 있는 경우, Refresh Token을 update함
        RefreshToken beforeRefreshToken = refreshTokenRepository.findOneById(id);

        if (beforeRefreshToken !=null){
//            logger.info("Before RefreshToken is exist.");

            refreshTokenRepository.updateRefreshToken(beforeRefreshToken.getIdx(),tokenDTO.getRefreshToken(),  expiration);
        }else{
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(id)
                    .token(tokenDTO.getRefreshToken())
                    .expiredAt(expiration)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
    }


    @Transactional
    public boolean registerMember(MemberRegisterDto registerDto ,HttpServletRequest request) {

        String clientIP = request.getHeader("X-Forwarded-For");

        //activate false setting
        LocalDateTime now = LocalDateTime.now();

        Inspection inspection = new Inspection();
        inspection.setSavedAt(now);

        //Check Password valid
        logger.info(registerDto.getPassword()," : ", registerDto.getValidPassword());
        if (!registerDto.getPassword().equals(registerDto.getValidPassword())){
            throw new BadRequestException("Valid Password is not same");
        }

        //중복된 ID 가 존재하지 않는 경우 실행
        if (memberRepository.findById(registerDto.getId()) == null){
//            boolean useLetters = true;
//            boolean useNumbers = true;
//            String randomStr = RandomStringUtils.random(10, useLetters, useNumbers);

            // Todo : JWT 관리자 권한 확인
            String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
//            logger.info("temp password : {}", randomStr);

            Member member = Member.builder()
                    .id(registerDto.getId())
                    .password(encodedPassword)
                    .activation(false)
                    .name(registerDto.getName())
                    .build();

            //login_log table 생성
            LoginLog loginLog = LoginLog.builder()
                    .id(registerDto.getId())
                    .failCount(0)
                    .failDt(null)
                    .joinIp(null)
                    .joinDt(null)
                    .createDt(now)
                    .createdBy("stocker_front") // frontend 에서 header에 추가하도록 수정
                    .updatedDt(null)
                    .updatedBy(null)
                    .build();
            PasswordLog passwordLog = PasswordLog.builder()
                    .id(registerDto.getId())
                    .lastPassword(encodedPassword)
                    .updatedDt(now)
                    .build();


            //roles 저장
            memberRepository.save(member);
            loginLogRepository.save(loginLog);
            passwordLogRepository.save(passwordLog);
//            logger.info("Roles : {}",registerDto.getRoles());
            List<String> roles = new ArrayList<String>();
            roles.add("ROLE_USER");

            saveRoles(roles, registerDto.getId());

            inspection.setContent(String.format("회원 가입 완료  || 가입 ID : %s || IP Address : %s",registerDto.getId(),clientIP));
            inspectionRepository.save(inspection);
            return true;
        }else {
            //422
            throw new UnprocessableEntityException("중복된 사용자가 존재합니다");
        }
    }

    public TokenDTO loginRequest(MemberLoginDto memberLoginDto, HttpServletRequest request) {

        Member loginMember = memberRepository.findById(memberLoginDto.getId());
        LocalDateTime now = LocalDateTime.now();
        String clientIP = request.getHeader("X-Forwarded-For");

        Inspection inspection = new Inspection();
        inspection.setSavedAt(now);

        if (loginMember ==null){
            throw new UnauthorizedException("Invalid ID/Password!");
        }
        if (!loginMember.getActivation()){
            logger.info("Login request to Locked Account : {}",loginMember.getId());
            throw new LockedException("계정이 잠겨있습니다. 관리자에서 문의하세요.");
        }

        Integer idx = loginMember.getIdx();
        UserDetails userDetails = customDetailsService.loadUserByUsername(memberLoginDto.getId());

        LoginLog loginLog = loginLogRepository.findLoginLogByMemberId(memberLoginDto.getId());
        //password 점검, password 불인치 횟수가 5인 경우 member.activation = false
        if(!passwordEncoder.matches(memberLoginDto.getPassword(), userDetails.getPassword())) {
            logger.error("Password mismatch for user: {}", memberLoginDto.getId());

            if (loginLog.getFailCount() < 5){
                loginLog.setFailCount(loginLog.getFailCount()+1);
                loginLog.setFailDt(LocalDateTime.now());

                updateLoginLog(loginLog);
                inspection.setContent(String.format("Login Fail over 5 times || ID : %s || IP Address : %s", memberLoginDto.getId(), clientIP));
                inspectionRepository.save(inspection);
                throw new UnauthorizedException("Invalid ID/Password!");

            } else{
                loginMember.setActivation(false);
                memberRepository.save(loginMember);
                throw new UnauthorizedException("Your account has been deactivated because you entered your password incorrectly 5 times");
            }
        }


        CreateTokenDto createTokenDto = new CreateTokenDto();
        createTokenDto.setMemberIdx(idx);
        createTokenDto.setName(loginMember.getName());


        // 사용자 인증을 위한 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), memberLoginDto.getPassword(), userDetails.getAuthorities()
        );
        //인증 수행, 인증 실행시 CustomUserDetailService class에서 loadUserbyUserName 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 SecurityContextHolder에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        createTokenDto.setAuthentication(authentication);
        // JWT 생성
        TokenDTO jwt = jwtProvider.createToken(createTokenDto);

        //Login IP, DATETIME 저장
        loginLog.setJoinIp(clientIP);
        loginLog.setJoinDt(now);
        loginLog.setFailCount(0);
        updateLoginLog(loginLog);

        inspection.setContent(String.format("Login || ID : %s || IP Address : %s", memberLoginDto.getId(), clientIP));
        inspectionRepository.save(inspection);
        // JWT와 헤더를 포함한 응답 반환
        return jwt;
    }
    
    //계정 생성 시 member roles 정보 저장
//    @Transactional
    protected void saveRoles(List<String> roles, String id){
        Integer idx = memberRepository.findIdxById(id);
        List<Integer> roleIds = roleInfoRepository.findRoleIdsByRoleNames(roles);
        roleSaver.saveRole(roleInfoRepository, memberRoleRepository, roleIds, idx);

    }

//    @Transactional
//    public APIKeyDto generateKey(String token, KeyGenDto keyGenDto){
//
//        //todo : token 값 검증 기능 추가
//        Claims payload = jwtProvider.parseClaims(token);
//        Object rolesObject = payload.get("roles");
//
//        // ADMIN 권한 확인
//        if (!checker.checkRoleLevelTwo(rolesObject)){
//            throw new ForbiddenException("You do not have permission to access this resource.");
//        }else {
//        //todo : expiration 값 조정하기
//            String key = UUID.randomUUID().toString();
////            logger.info("Key : {}",key);
//            LocalDateTime now = LocalDateTime.now();
////            logger.info("Now : {}",now);
//            APIKeyDto apiKeyDto = new APIKeyDto();
//            ApiKey apiKey = ApiKey.builder()
//                                .key_string(key)
//                                .expiredAt(now)
//                                .requester(keyGenDto.getId())
//                                .build();
//            apiKeyRepository.save(apiKey);
//            apiKeyDto.setApiKey(key);
//            apiKeyDto.setExpiredAt(now);
//            return apiKeyDto;
//        }
//    }

    //Api key를 발급 받는 기능 -> /api/... url 에 필터를 추가하기 때문에 auth 에서 발급
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateLoginLog(LoginLog loginLog){
        loginLogRepository.saveAndFlush(loginLog);
    }


}
