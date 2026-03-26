package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record IngestionRequest(
        Long siteId,
        String title,
        String sourceName,
        String indicatorValue,
        String rawText,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime collectedAt
) {}