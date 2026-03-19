package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RealtimeSummaryResponse(
        long hourlyCount,
        long activeEngines,
        List<AlertResponse> latestAlerts
) {
    public record AlertResponse(
            Long parsedId,
            String leakTitle,
            LocalDateTime parsedAt
    ) {}
}