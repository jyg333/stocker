package com.stocker.backend.utils;

import com.stocker.backend.exceptionHandling.UnauthorizedException;
import com.stocker.backend.keys.KeyPairManager;
import com.stocker.backend.model.TokenDTO;
import com.stocker.backend.model.dto.CreateTokenDto;
//import com.stocker.backend.otp.model.CreateOTPTokenDto;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Component annotation : Class를 Bean에 등록하기 위한 어노테이션 -> class_name을 camelcase로 변경한 것이 bean_id로 사용
@Component
public class JwtProvider {
    private static final Logger logger = LogManager.getLogger(JwtProvider.class);

    private final KeyPair keyPair;
    private static final String AUTHORITIES_KEY = "Authentication";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    @Value("${jwt.expiration_time}")
    private long validityInMilliseconds;// 24 hours
//    @Value("${keys.private}") String privateKeyPath;
//    @Value("${keys.public}") String publicKeyPath;

    @Autowired
    public JwtProvider(KeyPairManager keyPairManager,@Value("${keys.private}") String privateKeyPath,
    @Value("${keys.public}") String publicKeyPath
                       ) throws Exception {
        this.keyPair = keyPairManager.loadKeyPairFromPem(privateKeyPath, publicKeyPath);
    }


    public TokenDTO createToken(CreateTokenDto createTokenDto){

        Authentication authentication = createTokenDto.getAuthentication();

        String id = authentication.getName();
        Integer idx = createTokenDto.getMemberIdx();

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        //Todo : 권한을 list로 담도록 수정하기
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Claims claims = Jwts.claims().audience().add("stocker").and().subject(createTokenDto.getName()).add("uuid",idx).add("userId",id).add("roles",authorities).build();
//        logger.info("createToken Claims : {}",claims);
        String accessToken = Jwts.builder()
                .issuer("stocker")
                .subject("auth")
                .claims(claims)
                .expiration(validity)
                .issuedAt(now)
                .signWith(keyPair.getPrivate())
                .compact();

        //Refresh Token생성
        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .expiration(validity)
                .claim(AUTHORITIES_KEY,authorities)
                .signWith(keyPair.getPrivate())
                .compact();

        return TokenDTO.builder()
//                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Token Validation 방법
    public Boolean validateToken(String jwt) {

        try {
            Date now = new Date();

            Claims claims = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            Date token_expiration = claims.getExpiration();

//            logger.info("Token expiration data : {}", token_expiration);
            if (token_expiration.after(now)) {
//                logger.info("Token Validation is Success!");
                return true;
            } else {
                logger.info("Token Validation is Fail!");
                return false;
            }

        }
        catch (ExpiredJwtException e){
            return false;
        }catch (UnsupportedJwtException e) {
            logger.error("UnsupportedJwtException :", e);
            throw new UnauthorizedException("Token is unsupported");
        }
//        catch (SignatureException e){
//            throw new UnauthorizedException("Token is Signature is counterfeited");}
        catch (IllegalArgumentException e) {
            logger.error("IllegalArgument Exception : " + e);
//            throw new RuntimeException(e);
        }
        return false;
    }


    public Authentication getAuthentication(String accessToken){
        //Token decryption
        Claims claims = parseClaims(accessToken);
//        logger.info("getAuthentication Claims : {}",claims);

        //Todo : 변경
        if(claims.getSubject()==null) {
            throw new RuntimeException("Token has no Authorization.");
        }
        // UserDetails 객체를 만들어서 Authentication 리턴
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

//        UserDetails principal = new User(claims.getSubject(), "", authorities);
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);

//        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        return principal;
    }
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {

        //
//        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles) {
            authorities.add( () -> role );
        }
        return authorities;
    }


    public Claims parseClaims(String accessToken) {
        try{

            return Jwts.parser().verifyWith(keyPair.getPublic()).build().parseSignedClaims(accessToken).getPayload();
        }catch (ExpiredJwtException e){
            //Todo : 값 확인하기
            return e.getClaims();
        }
    }

}
