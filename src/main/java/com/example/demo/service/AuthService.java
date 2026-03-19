package com.example.demo.service;

import com.example.demo.dto.request.AuthRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.AdminUser;
import com.example.demo.global.utils.JwtProvider;
import com.example.demo.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AdminUserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse loginWithGoogle(AuthRequest request) {
        // 1. 구글 인증 코드로 유저 정보 획득 (가상 로직)
        String email = "admin@example.com";
        String name = "정현진";
        String googleId = "google_sub_12345";

        // 2. [API 1] 유저 조회 또는 신규 등록 (Upsert)
        AdminUser user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(AdminUser.createPendingUser(email, googleId, name)));

        // 3. JWT 토큰 발행
        String accessToken = jwtProvider.createToken(user.getEmail(), user.getRole());

        // 4. DTO 변환 후 반환
        return AuthResponse.of(accessToken, user);
    }
}