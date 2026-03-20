package com.example.demo.dto.response;

import java.util.List;

public record DashboardStatsResponse(
        long totalCount,
        List<SiteStatResponse> siteStats
) {
    public record SiteStatResponse(
            Long siteId,
            String sourceName,
            long count,
            double ratio
    ) {}
}