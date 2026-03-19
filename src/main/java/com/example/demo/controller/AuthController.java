package com.example.demo.controller;

import com.example.demo.dto.request.AuthRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ApiResponse<AuthResponse> googleLogin(@RequestBody @Valid AuthRequest request) {
        AuthResponse response = authService.loginWithGoogle(request);
        return ApiResponse.success(response);
    }
}