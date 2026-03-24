package com.example.demo.dto.response;

import java.util.List;

public record DashboardStatsResponse(
        long totalCount,                // [6] 총 누적 탐지건수
        List<DailyStatResponse> dailyStats, // [7] 일별 유출건수
        List<SiteStatResponse> siteStats     // [8] 사이트별 비중
) {
    // 날짜별 통계를 위한 레코드
    public record DailyStatResponse(
            String date,    // 예: "3/23"
            long count      // 해당 날짜 탐지 건수
    ) {}

    // 사이트별 비중을 위한 레코드
    public record SiteStatResponse(
            Long siteId,
            String sourceName,
            long count,
            double ratio
    ) {}
}