package com.example.demo.dto.response;

import java.util.List;

public record EngineStatusResponse(
        List<EngineInfo> engines
) {
    public record EngineInfo(
            Long siteId,
            String sourceName,
            String crawlerStatus,
            String lastConnectedAt
    ) {}
}