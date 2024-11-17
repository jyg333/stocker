package com.stocker.backend.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
@Component
// 두개의 Jwt filter 코드를 비굥하기 위해 임시적으로 사용 예정
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);
        
        //validateToken으로 검증, 정상 토큰일 경우,  Authentication을 가져와서securityContext 에 저장
        if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            Authentication authentication = jwtProvider.getAuthentication(jwt);
//            logger.info("Jwt Filter Authentication : "+authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            logger.info("SecurityContextHolder : {}"+ SecurityContextHolder.getContext().getAuthentication());

        }

        filterChain.doFilter(request,response);
    }
    // Request Header로 부터 Token 정보 조회
    private String resolveToken(HttpServletRequest request){

        //요청 ip address 정보
//        logger.info("Request from : "+request.getRemoteHost());

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        logger.info("baererToken : "+bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            //앞의  'Bearer '부분 제거
            return bearerToken.substring(7);
        }

        return null;
    }

}
