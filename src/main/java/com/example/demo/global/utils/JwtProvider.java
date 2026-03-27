package com.example.demo.global.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String salt;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        // 최신 버전도 Keys.hmacShaKeyFor는 동일
        this.secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * [API 1] 로그인 성공 시 토큰 생성 (최신 0.12.x 문법)
     */
    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email)           // setSubject -> subject
                .claim("role", role)      // claims.put -> claim
                .issuedAt(now)
                .expiration(validity)     // setExpiration -> expiration
                .signWith(secretKey)      // 알고리즘 생략 가능 (자동지정)
                .compact();
    }

    /**
     * 토큰에서 인증 정보 조회
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // 권한 정보 추출
        String role = claims.get("role", String.class);
        
        if (role == null || role.isEmpty()) {
            role = "ROLE_USER";
        }

        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(role));

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰 유효성 체크 (최신 verifyWith 방식)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("JWT 검증 실패: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰 파싱 (최신 getPayload 방식)
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // getBody -> getPayload
    }
}