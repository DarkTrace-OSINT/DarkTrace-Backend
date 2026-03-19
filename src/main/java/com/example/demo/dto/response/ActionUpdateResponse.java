package com.example.demo.dto.response;

import java.time.LocalDateTime;

/**
 * [API 5] 조치 업데이트 응답 규격
 */
public record ActionUpdateResponse(
        Long responseId,
        LocalDateTime updatedAt
) {}