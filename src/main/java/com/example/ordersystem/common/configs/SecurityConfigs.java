package com.example.ordersystem.common.configs;


import com.example.ordersystem.common.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // PRE : 사전, POST: 사후 검증
public class SecurityConfigs {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtAuthFilter jwtAuthFilter) throws Exception {
        return httpSecurity
                .csrf().disable()
                .cors().and() // CORS 활성화  // 다른 도메인끼리 통신 불가
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/member/create ", "/" ," /doLogin")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                // 세션 로그인이 아닌 stateLess한 TOKEN 값을 사용하겠다는 의미
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 로그인 시 사용자는 서버로부터 토큰을 발급받고,
                // 매 요청마다 해당 토큰을 http header 넣어 요청
                // 아래 코든는 사용자로부터 받아온 토큰이 정상인지 아닌지를 검증하는 코드
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
