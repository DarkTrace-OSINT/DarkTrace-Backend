package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * [API 5] 조치 업데이트 요청 규격
 */
public record ActionUpdateRequest(

        @NotNull(message = "parseId 필수")
        Long parseId,
        @NotNull(message = "adminId 필수")
        Long adminId,
        @NotNull(message = "actionStatus 필수")
        String actionStatus,
        @NotBlank(message = "actionNote 필수")
        String actionNote
) {}