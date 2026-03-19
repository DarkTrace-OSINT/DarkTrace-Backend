package com.example.demo.dto.request;

/**
 * [API 5] 조치 업데이트 요청 규격
 */
public record ActionUpdateRequest(
        Long parsedId,      // 대상 유출 데이터 ID
        Long adminId,       // 조치한 관리자 ID
        String actionStatus, // 상태: OPEN, RESOLVED 등
        String actionNote    // 관리자 조치 메모
) {}