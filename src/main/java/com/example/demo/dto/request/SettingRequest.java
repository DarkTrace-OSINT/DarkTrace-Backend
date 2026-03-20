package com.example.demo.dto.request;

import java.util.List;

/**
 * [API 7] 시스템 및 텔레그램 설정 수정 요청
 */
public record SettingRequest(
        String telegramBotToken,
        String telegramChatId,
        List<String> keywords
) {}