package com.example.demo.repository;

import com.example.demo.entity.ActionStatus;
import com.example.demo.entity.IndicatorType;
import com.example.demo.entity.ParsedThreatData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ParsedThreatDataRepository extends JpaRepository<ParsedThreatData, Long> {

    // 시간 기준 탐지 건수
    long countByCreatedAtAfter(LocalDateTime dateTime);

    // 사이트별 + 시간 기준 탐지 건수
    long countBySourceNameAndCreatedAtAfter(String sourceName, LocalDateTime dateTime);

    // 조치 상태별 건수 (ParsedThreatData에서 직접 셈)
    long countByActionStatus(ActionStatus status);

    // 최신 알림 5건
    List<ParsedThreatData> findTop5ByOrderByCreatedAtDesc();

    // 키워드 + 유형 + 상태 필터링
    Page<ParsedThreatData> findByIndicatorValueContainingAndIndicatorTypeAndActionStatus(
            String keyword, IndicatorType type, ActionStatus status, Pageable pageable
    );
}