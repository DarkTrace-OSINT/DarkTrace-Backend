package com.example.demo.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_PARAMETER(400, "BAD_REQUEST", "잘못된 요청 파라미터입니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증되지 않은 사용자입니다."),
    NOT_FOUND(404, "NOT_FOUND", "해당 데이터를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}