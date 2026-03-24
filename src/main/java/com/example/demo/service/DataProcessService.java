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
     * [API 2] 대시보드 통계 데이터 조회 (실제 DB 데이터 연동 완료)
     */
    public DashboardStatsResponse getDashboardStatistics() {
        // 1. 총 누적 건수 (중복 제거 없이 전체 카운트)
        long totalCount = indicatorRepository.count();
        if (totalCount == 0) return new DashboardStatsResponse(0, List.of(), List.of());

        // 2. 최근 7일간의 일별 유출 건수 (Repository 신규 쿼리 호출)
        List<Object[]> dailyResults = indicatorRepository.findDailyStatsLast7Days();
        List<DashboardStatsResponse.DailyStatResponse> dailyStats = dailyResults.stream()
                .map(result -> new DashboardStatsResponse.DailyStatResponse(
                        (String) result[0],      // "03/24" 형태의 날짜
                        ((Number) result[1]).longValue() // 해당 날짜의 건수
                )).toList();

        // 3. 사이트별 비중 (기존 로직 유지 및 최적화)
        List<Object[]> siteResults = indicatorRepository.countGroupBySiteId();
        List<DashboardStatsResponse.SiteStatResponse> siteStats = siteResults.stream()
                .map(result -> {
                    Long siteId = (Long) result[0];
                    long count = ((Number) result[1]).longValue();
                    String sourceName = siteRepository.findById(siteId)
                            .map(TargetSite::getSourceName).orElse("Unknown");

                    double ratio = (totalCount > 0) ? (double) count / totalCount * 100 : 0;
                    return new DashboardStatsResponse.SiteStatResponse(
                            siteId, sourceName, count, Math.round(ratio * 10.0) / 10.0 // 소수점 첫째 자리까지
                    );
                }).toList();

        // 4. 최종 조립된 DTO 리턴
        return new DashboardStatsResponse(totalCount, dailyStats, siteStats);
    }

    /**
     * [API 3] 실시간 탐지 현황 요약
     */
    public RealtimeSummaryResponse getRealtimeSummary() {
        // 1. 기준 시간 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.with(java.time.LocalTime.MIN); // 오늘 00:00
        LocalDateTime oneHourAgo = now.minusHours(1);               // 1시간 전
        LocalDateTime weekStart = now.with(java.time.DayOfWeek.MONDAY).with(java.time.LocalTime.MIN); // 이번주 월요일

        // 2. 통계 데이터 집계 (Repository 호출)
        long totalCount = parsedDataRepository.countByCreatedAtAfter(todayStart);     // 오늘 총합
        long lastHourCount = parsedDataRepository.countByCreatedAtAfter(oneHourAgo); // 최근 1시간
        long thisWeekCount = parsedDataRepository.countByCreatedAtAfter(weekStart);   // 이번 주

        long openCount = incidentRepository.countByActionStatus("OPEN");             // 미조치
        long resolvedCount = incidentRepository.countByActionStatus("RESOLVED");     // 조치완료

        // 크리티컬 기준: 오늘 탐지된 것 중 'BreachForums'에서 온 데이터라고 가정 (팀장님 기준에 따라 수정 가능)
        long criticalCount = parsedDataRepository.countBySourceNameAndCreatedAtAfter("BreachForums", todayStart);

        // 3. 실시간 위협 목록 (최근 5건)
        List<RealtimeSummaryResponse.ThreatResponse> threats = parsedDataRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(data -> {
                    String timeLabel = (data.getCreatedAt() != null)
                            ? data.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                            : "N/A";

                    // 조치 상태 조회 (연결된 인시던트가 없으면 기본 "OPEN")
                    String status = incidentRepository.findByParsedId(data.getId())
                            .map(IncidentResponse::getActionStatus)
                            .orElse("OPEN");

                    return new RealtimeSummaryResponse.ThreatResponse(
                            data.getId(),
                            data.getIndicatorValue(),
                            data.getSourceName(),
                            timeLabel,
                            status
                    );
                })
                .toList();

        // 4. 최종 DTO 조립
        return new RealtimeSummaryResponse(
                new RealtimeSummaryResponse.SummaryResponse(
                        totalCount, lastHourCount, thisWeekCount, openCount, resolvedCount, criticalCount
                ),
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

        keywordRepository.deleteAllInBatch();

        List<DetectionKeyword> newKeywords = request.keywords().stream()
                .distinct()
                .map(DetectionKeyword::of)
                .toList();

        keywordRepository.saveAll(newKeywords);

        return new SettingResponse(savedSetting.getId(), savedSetting.getUpdatedAt());
    }
}