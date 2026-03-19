package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "인증 코드는 필수입니다.")
        String code
) {}