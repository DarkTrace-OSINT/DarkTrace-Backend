package com.example.demo.dto.response;

import java.time.LocalDateTime;

/**
 * [API 7] 설정 수정 응답 (명세서 2페이지 규격)
 */
public record SettingResponse(
        Long settingId,
        LocalDateTime updatedAt
) {}