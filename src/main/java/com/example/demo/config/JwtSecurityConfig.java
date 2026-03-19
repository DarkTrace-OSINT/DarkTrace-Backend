package com.example.demo.config;

// 1. 실제 파일이 들어있는 경로로 임포트 (gloval 대신 실제 폴더명 확인 필요)
import com.example.demo.global.filter.JwtAuthenticationFilter; // 경로 확인!
import com.example.demo.global.utils.JwtProvider;             // 경로 확인!
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    // 2. 임포트한 이름인 JwtProvider로 통일!
    private final JwtProvider jwtProvider;

    @Override
    public void configure(HttpSecurity http) {
        // 3. 임포트한 이름인 JwtAuthenticationFilter로 통일!
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(jwtProvider);

        // 시큐리티 필터 체인에 우리 필터를 등록
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}