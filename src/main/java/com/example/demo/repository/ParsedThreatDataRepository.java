package com.example.demo.repository;

import com.example.demo.entity.ParsedThreatData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ParsedThreatDataRepository extends JpaRepository<ParsedThreatData, Long> {

    // [API 3] 최근 1시간 내 탐지 건수 조회를 위해 필요
    long countByCreatedAtAfter(LocalDateTime dateTime);
    // 특정 사이트(예: BreachForums)를 크리티컬로 분류할 경우
    long countBySourceNameAndCreatedAtAfter(String sourceName, LocalDateTime dateTime);
    // [API 3] 최신 알림 5건 조회를 위해 필요
    List<ParsedThreatData> findTop5ByOrderByCreatedAtDesc();
}