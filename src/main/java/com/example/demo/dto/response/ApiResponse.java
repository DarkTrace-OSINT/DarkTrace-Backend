package com.example.demo.dto.response;

public record ApiResponse<T>(
        int status,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> error(int status, String code, String message) {
        return new ApiResponse<>(status, code, message, null);
    }
}