package com.example.demo.dto.response;

import java.util.List;

public record RealtimeSummaryResponse(
        SummaryResponse summary,      // 낱개 필드 대신 객체로 묶음
        List<ThreatResponse> threats  // latestAlerts 대신 threats
) {
    public record SummaryResponse(
            long criticalCount,
            long totalCount
    ) {}

    public record ThreatResponse(
            Long id,
            String indicatorValue,
            String sourceName,
            String detectedAt      // LocalDateTime 대신 String (포맷팅용)
    ) {}
}