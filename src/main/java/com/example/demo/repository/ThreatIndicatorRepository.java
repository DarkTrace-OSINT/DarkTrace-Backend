package com.example.demo.repository;

import com.example.demo.dto.request.ThreatSearchRequest;
import com.example.demo.dto.response.ThreatSearchResponse;
import com.example.demo.entity.ThreatIndicator;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ThreatIndicatorRepository extends JpaRepository<ThreatIndicator, Long> {

    // [API 2-1] 사이트별 통계 조회
    @Query("SELECT t.siteId, COUNT(t) FROM ThreatIndicator t GROUP BY t.siteId")
    List<Object[]> countGroupBySiteId();

    // [API 2-2] 최근 7일간의 날짜별 유출 건수 조회
    // Native Query를 사용하여 DB에서 직접 날짜별로 묶어서 7줄만 가져옴.
    @Query(value = "SELECT DATE_FORMAT(created_at, '%m/%d') as date, COUNT(*) as count " +
            "FROM threat_indicators " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY DATE_FORMAT(created_at, '%m/%d') " +
            "ORDER BY date ASC", nativeQuery = true)
    List<Object[]> findDailyStatsLast7Days();

    // [API 4]
    default Page<ThreatSearchResponse.ThreatIndicatorResponse> searchIndicators(
            ThreatSearchRequest request, Pageable pageable) {
        return null;
    }
}