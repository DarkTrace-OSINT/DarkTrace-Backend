package com.example.demo.dto.response;

import java.util.List;

/**
 * [API 6] 엔진 상태 모니터링 응답 규격
 */
public record EngineStatusResponse(
        List<EngineInfo> engines
) {
    public record EngineInfo(
            Long siteId,
            String sourceName,
            String crawlerStatus
    ) {}
}