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
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long hourlyCount = parsedDataRepository.countByCreatedAtAfter(oneHourAgo);
        long activeEngines = siteRepository.countByCrawlerStatus("ALIVE");

        List<RealtimeSummaryResponse.AlertResponse> latestAlerts = parsedDataRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(data -> new RealtimeSummaryResponse.AlertResponse(
                        data.getId(),
                        data.getLeakTitle(),
                        data.getCreatedAt()
                ))
                .toList();

        return new RealtimeSummaryResponse(hourlyCount, activeEngines, latestAlerts);
    }

    /**
     /**
     * [API 4] 유출 데이터 검색 및 목록 조회
     */
    public ThreatSearchResponse searchThreats(ThreatSearchRequest request) {
        // 1. 페이지 요청 객체 생성 (기존과 동일)
        Pageable pageable = PageRequest.of(request.page(), request.size());

        // 2. 레포지토리 호출
        Page<ThreatSearchResponse.ThreatIndicatorResponse> resultPage =
                indicatorRepository.searchIndicators(request, pageable);

        // 3. [수정] null 방어 로직 추가
        // 만약 결과가 null이면 빈 리스트와 0페이지 정보를 담은 객체를 즉시 반환
        if (resultPage == null) {
            return new ThreatSearchResponse(java.util.Collections.emptyList(), 0);
        }

        // 4. 결과가 있을 때만 정상 반환
        return new ThreatSearchResponse(
                resultPage.getContent(),
                resultPage.getTotalPages()
        );
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
     * [API 6] 엔진 상태 모니터링 목록 조회
     */
    public EngineStatusResponse getEngineStatuses() {
        List<EngineStatusResponse.EngineInfo> engineInfos = siteRepository.findAll().stream()
                .map(site -> new EngineStatusResponse.EngineInfo(
                        site.getId(),
                        site.getSourceName(),
                        site.getCrawlerStatus()
                ))
                .toList();

        return new EngineStatusResponse(engineInfos);
    }

    /**
     * [API 7] 시스템 설정 업데이트
     */
    @Transactional
    public SettingResponse updateSystemSettings(SettingRequest request) {
        // [사진 13, 14 대응] findFirstByOrderBySettingIdAsc -> findFirstByOrderByIdAsc
        // 만약 레포지토리에 해당 메서드가 없다면, findFirstByOrderByIdAsc로 이름을 맞춰주세요.
        SystemSetting setting = settingRepository.findFirstByOrderByIdAsc()
                .orElseGet(SystemSetting::new);

        setting.updateConfig(request.telegramBotToken(), request.telegramChatId());
        SystemSetting savedSetting = settingRepository.save(setting);

        keywordRepository.deleteAll();
        List<DetectionKeyword> newKeywords = request.keywords().stream()
                .map(DetectionKeyword::of) // [수정] new DetectionKeyword(k) -> of(k)
                .toList();
        keywordRepository.saveAll(newKeywords);

        // [사진 13 대응] getSettingId() -> getId()
        return new SettingResponse(savedSetting.getId(), savedSetting.getUpdatedAt());
    }
}