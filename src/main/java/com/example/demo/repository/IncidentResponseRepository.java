package com.example.demo.repository;

import com.example.demo.entity.IncidentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentResponseRepository extends JpaRepository<IncidentResponse, Long> {
    // 조치 상태별 카운트 (OPEN, RESOLVED)
    long countByActionStatus(String actionStatus);
    // 특정 유출 데이터(parsedId)에 대한 조치 이력을 찾기 위해 필요
    Optional<IncidentResponse> findByParsedId(Long parsedId);

}