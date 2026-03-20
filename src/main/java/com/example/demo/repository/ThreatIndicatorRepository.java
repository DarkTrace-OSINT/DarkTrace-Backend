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

    // [API 2] 사이트별 통계 조회를 위한 집계 쿼리
    // 별도 DTO 클래스 없이 Object 배열로 받아서 Service에서 가공합니다.
    @Query("SELECT t.siteId, COUNT(t) FROM ThreatIndicator t GROUP BY t.siteId")
    List<Object[]> countGroupBySiteId();

    // [API 4] 복잡한 필터링 조회를 위한 커스텀 쿼리 (Default Method로 구현하여 Impl 생략)
    default Page<ThreatSearchResponse.ThreatIndicatorResponse> searchIndicators(
            ThreatSearchRequest request, Pageable pageable) {

        // 실제 구현 시에는 여기서 JPAQueryFactory를 사용하여
        // keyword, siteId, actionStatus 등을 동적으로 조인 및 필터링합니다.
        // (Querydsl 설정이 되어 있으므로 여기서 바로 쿼리 작성이 가능합니다.)

        return null; // (실제 쿼리 로직은 팀장님 환경의 Q클래스 생성 후 완성됩니다.)
    }
}