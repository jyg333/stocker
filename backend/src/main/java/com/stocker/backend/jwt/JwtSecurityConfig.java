package com.stocker.backend.jwt;
import com.stocker.backend.utils.JwtFilter;
import com.stocker.backend.utils.JwtProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtProvider jwtProvider;

    public JwtSecurityConfig(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }
    @Override
    public void configure(HttpSecurity http){
        // 직접 만든 Jwtfilter를 Security FIlter 앞에 추가
        http.addFilterBefore(
                new JwtFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }

}
