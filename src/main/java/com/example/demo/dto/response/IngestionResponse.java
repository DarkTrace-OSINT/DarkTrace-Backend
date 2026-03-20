package com.example.demo.dto.response;

public record IngestionResponse(
        Long ingestId,
        boolean isAlertSent
) {}