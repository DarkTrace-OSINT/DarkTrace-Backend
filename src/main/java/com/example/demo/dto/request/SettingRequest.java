package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * [API 7] 시스템 및 텔레그램 설정 수정 요청
 */
public record SettingRequest(
        String telegramBotToken,
        String telegramChatId,
        @JsonProperty("isAlertEnabled") // JSON의 키값과 매핑을 명확히 함
        boolean isAlertEnabled,
        List<String> keywords
) {}