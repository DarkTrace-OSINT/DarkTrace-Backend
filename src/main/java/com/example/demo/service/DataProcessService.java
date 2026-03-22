package com.example.demo.service;

import com.example.demo.dto.request.ActionUpdateRequest;
import com.example.demo.dto.request.SettingRequest;
import com.example.demo.dto.request.ThreatSearchRequest;
import com.example.demo.dto.response.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // 👈 [수정] java.awt.print.Pageable은 지우고 이걸로!
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataProcessService {

    private final ThreatIndicatorRepository indicatorRepository;
    private final TargetSiteRepository siteRepository;
    private final ParsedThreatDataRepository parsedDataRepository;
    private final IncidentResponseRepository incidentRepository;
    private final SystemSettingRepository settingRepository;
    private final DetectionKeywordRepository keywordRepository;

    /**
     * [API 2] 대시보드 통계 데이터 조회
     */
    public DashboardStatsResponse getDashboardStatistics() {
        long totalCount = indicatorRepository.count();
        if (totalCount == 0) return new DashboardStatsResponse(0, List.of());

        List<Object[]> results = indicatorRepository.countGroupBySiteId();

        List<DashboardStatsResponse.SiteStatResponse> siteStats = results.stream()
                .map(result -> {
                    Long siteId = (Long) result[0];
                    long count = (long) result[1];
                    // [사진 11 대응] 엔티티 필드명 sourceName 확인 완료
                    String sourceName = siteRepository.findById(siteId)
                            .map(TargetSite::getSourceName).orElse("Unknown");

                    double ratio = (double) count / totalCount * 100;
                    return new DashboardStatsResponse.SiteStatResponse(
                            siteId, sourceName, count, Math.round(ratio * 10.0) / 10.0
                    );
                }).toList();

        return new DashboardStatsResponse(totalCount, siteStats);
    }

    /**
     * [API 3] 실시간 탐지 현황 요약
     */
    public RealtimeSummaryResponse getRealtimeSummary() {
        // 1. 기존 데이터 조회 로직 (그대로 유지)
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long hourlyCount = parsedDataRepository.countByCreatedAtAfter(oneHourAgo);
        long activeEngines = siteRepository.countByCrawlerStatus("ALIVE");

        // 2. 리스트 변환
        List<RealtimeSummaryResponse.ThreatResponse> threats = parsedDataRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(data -> {
                    // [수정] createdAt이 null이면 현재 시간으로 대체해서 500 에러 방지
                    String timeLabel = (data.getCreatedAt() != null)
                            ? data.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                            : java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

                    return new RealtimeSummaryResponse.ThreatResponse(
                            data.getId(),
                            data.getIndicatorValue() != null ? data.getIndicatorValue() : "N/A",
                            data.getSourceName() != null ? data.getSourceName() : "Unknown",
                            timeLabel
                    );
                })
                .toList();

        return new RealtimeSummaryResponse(
                new RealtimeSummaryResponse.SummaryResponse(hourlyCount, activeEngines),
                threats
        );
    }

    /**
     * [API 4] 위협 데이터 검색 (500 에러 수정본)
     */
    public ThreatSearchResponse searchThreats(ThreatSearchRequest request) {
        // 1. 페이지/사이즈 null 방어 (PowerShell에서 안 보낼 때 대비)
        int page = (request.page() != null) ? request.page() : 0;
        int size = (request.size() != null) ? request.size() : 10;
        Pageable pageable = PageRequest.of(page, size);

        // 2. 전체 데이터 조회
        Page<ParsedThreatData> resultPage = parsedDataRepository.findAll(pageable);

        if (resultPage == null || resultPage.isEmpty()) {
            return new ThreatSearchResponse(java.util.Collections.emptyList(), 0);
        }

        // 3. 변환 로직
        List<ThreatSearchResponse.ThreatIndicatorResponse> content = resultPage.getContent().stream()
                .map(data -> {
                    // 날짜가 null이면 오늘 날짜로 표시 (서버 다운 방지)
                    String dateLabel = (data.getCreatedAt() != null)
                            ? data.getCreatedAt().toLocalDate().toString()
                            : java.time.LocalDate.now().toString();

                    return new ThreatSearchResponse.ThreatIndicatorResponse(
                            data.getId(),
                            data.getIndicatorValue() != null ? data.getIndicatorValue() : "N/A",
                            "EMAIL",
                            data.getSourceName() != null ? data.getSourceName() : "Unknown",
                            dateLabel,
                            "OPEN"
                    );
                })
                .toList();

        return new ThreatSearchResponse(content, resultPage.getTotalPages());
    }

    /**
     * [API 5] 위협 조치 업데이트
     */
    @Transactional
    public ActionUpdateResponse updateThreatAction(ActionUpdateRequest request) {
        // [사진 12 대응] createNew -> createInitial로 명칭 수정
        IncidentResponse incident = incidentRepository.findByParsedId(request.parsedId())
                .orElseGet(() -> IncidentResponse.createInitial(request.parsedId(), request.adminId()));

        incident.updateAction(request.actionStatus(), request.actionNote(), request.adminId());
        IncidentResponse saved = incidentRepository.save(incident);

        return new ActionUpdateResponse(saved.getId(), saved.getUpdatedAt());
    }

    /**
     * [API 6] 엔진 상태 모니터링 목록 조회 (수정본)
     */
    public EngineStatusResponse getEngineStatuses() {
        List<EngineStatusResponse.EngineInfo> engineInfos = siteRepository.findAll().stream()
                .map(site -> new EngineStatusResponse.EngineInfo(
                        site.getId(),
                        site.getSourceName(),
                        site.getCrawlerStatus(),
                        site.getUpdatedAt() != null ?
                                site.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) :
                                "N/A"
                ))
                .toList();

        return new EngineStatusResponse(engineInfos);
    }

    /**
     * [API 7] 시스템 설정 업데이트
     */
    @Transactional
    public SettingResponse updateSystemSettings(SettingRequest request) {

        SystemSetting setting = settingRepository.findFirstByOrderByIdAsc()
                .orElseGet(SystemSetting::new);

        setting.updateConfig(request.telegramBotToken(), request.telegramChatId());
        SystemSetting savedSetting = settingRepository.save(setting);

        keywordRepository.deleteAll();
        List<DetectionKeyword> newKeywords = request.keywords().stream()
                .distinct()
                .map(DetectionKeyword::of)
                .toList();
        keywordRepository.saveAll(newKeywords);

        return new SettingResponse(savedSetting.getId(), savedSetting.getUpdatedAt());
    }
}