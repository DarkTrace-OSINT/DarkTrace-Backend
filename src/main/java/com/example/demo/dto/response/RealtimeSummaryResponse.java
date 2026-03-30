package com.example.demo.dto.response;

import java.util.List;

public record RealtimeSummaryResponse(
        SummaryResponse summary,
        List<ThreatResponse> threats
) {
    public record SummaryResponse(
            long totalCount,
            long lastHourCount,
            long thisWeekCount,
            long openCount,
            long resolvedCount,
            long criticalCount
    ) {}

    public record ThreatResponse(
            Long id,
            String indicatorValue,
            String sourceName,
            String detectedAt,    // "20:15" 형태의 포맷팅된 문자열
            String actionStatus   // "OPEN", "RESOLVED" 등의 상태값
    ) {}
}