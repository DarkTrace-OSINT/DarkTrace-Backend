package com.example.demo.dto.response;

import java.util.List;

public record ThreatSearchResponse(
        List<ThreatIndicatorResponse> content,
        int totalPages
) {
    public record ThreatIndicatorResponse(
            Long indicatorId,
            String indicatorValue,
            String indicatorType,
            String sourceName,
            String detectedAt,
            String actionStatus
    ) {}
}