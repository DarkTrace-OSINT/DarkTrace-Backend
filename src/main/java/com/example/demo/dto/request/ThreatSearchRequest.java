package com.example.demo.dto.request;

public record ThreatSearchRequest(
        String keyword,
        Long siteId,
        String indicatorType,
        String actionStatus,
        int page,
        int size
) {}