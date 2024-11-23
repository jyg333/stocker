package com.stocker.backend.config;


import com.stocker.backend.jwt.JwtAccessDeniedHandler;
import com.stocker.backend.jwt.JwtAuthenticationEntryPoint;
import com.stocker.backend.jwt.JwtSecurityConfig;
import com.stocker.backend.utils.JwtFilter;
import com.stocker.backend.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;


/*Sping Security 5.4 이후 부터는 WebSecurityConfigurerAdapter 가 Deprecated 되었기 때문에 해당 클래스를 사용하지 않고 아래와 같이 정의해서 사용함
* HttpSecurity : 세부적인 보안기능을 설정할 수 있는 api 제공*/
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final CorsFilter corsFilter;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) //개발 환경에서만 설정 적용
                //api key filter 추가
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, CorsFilter.class)
//                .addFilterBefore(new ApiKeyFilter(apiKeyService), BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // 로그인, 회원가입, reissue API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/auth/**","/auth/reissue","/api/**","/otp/**").permitAll()
                        .requestMatchers("/member/member-list").hasAnyAuthority("ROLE_ADMIN","ROLE_OBSERVER")
                        .requestMatchers("/inspection/**").hasAnyAuthority("ROLE_ADMIN","ROLE_OBSERVER")
//                        .requestMatchers("/auth/reissue").access(new WebExpressionAuthorizationManager("hasRole('ADMIN')"))
//                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated() // 로그인한 모든 사용자에게 허용, anyRequest 가장 마지막에 적용해야하는 부분임
                )

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                // enable h2-console
//                .headers(headers ->
//                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//                )

                .with(new JwtSecurityConfig(jwtProvider), customizer -> {});
        return http.build();

}
}
