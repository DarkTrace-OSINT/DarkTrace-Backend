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


    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countBySourceNameAndCreatedAtAfter(String sourceName, LocalDateTime dateTime);
    long countByActionStatus(ActionStatus status);

    List<ParsedThreatData> findTop5ByOrderByCreatedAtDesc();

    Page<ParsedThreatData> findByIndicatorValueContaining(
            String keyword, Pageable pageable
    );

    Page<ParsedThreatData> findByIndicatorValueContainingAndIndicatorType(
            String keyword, IndicatorType type, Pageable pageable
    );

    Page<ParsedThreatData> findByIndicatorValueContainingAndActionStatus(
            String keyword, ActionStatus status, Pageable pageable
    );

    Page<ParsedThreatData> findByIndicatorValueContainingAndIndicatorTypeAndActionStatus(
            String keyword, IndicatorType type, ActionStatus status, Pageable pageable
    );
}