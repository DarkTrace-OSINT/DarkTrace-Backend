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
    // 필드가 siteId(숫자)에서 site(객체)로 바뀌었으므로 t.site.id로 참조.
    @Query("SELECT t.site.id, COUNT(t) FROM ThreatIndicator t GROUP BY t.site.id")
    List<Object[]> countGroupBySiteId();

    // [API 2-2] 최근 7일간의 날짜별 유출 건수 조회
    // Native Query는 DB의 실제 컬럼명(site_id)을 따르므로 그대로 둬도 되지만,
    // SELECT와 GROUP BY 형식을 맞춘 아까의 최종본을 유지.
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