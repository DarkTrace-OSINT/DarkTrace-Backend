package com.example.demo.dto.response;

import java.util.List;

public record RealtimeSummaryResponse(
        SummaryResponse summary,
        List<ThreatResponse> threats
) {
    public record SummaryResponse(
            long totalCount,      // [1] 오늘 총합
            long lastHourCount,   // [2] 최근 1시간 탐지
            long thisWeekCount,   // [3] 이번 주 탐지
            long openCount,       // [4] Open (미조치)
            long resolvedCount,   // [5] Resolved (조치완료)
            long criticalCount    // 크리티컬 위협
    ) {}

    public record ThreatResponse(
            Long id,
            String indicatorValue,
            String sourceName,
            String detectedAt,    // "20:15" 형태의 포맷팅된 문자열
            String actionStatus   // "OPEN", "RESOLVED" 등의 상태값
    ) {}
}