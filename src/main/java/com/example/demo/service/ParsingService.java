package com.example.demo.service;

import com.example.demo.dto.request.IngestionRequest;
import com.example.demo.dto.request.ThreatSearchRequest;
import com.example.demo.dto.response.IngestionResponse;
import com.example.demo.dto.response.ThreatSearchResponse;
import com.example.demo.entity.*; // 모든 엔티티 임포트
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParsingService {

    // [체크] 레포지토리 이름도 RawCollectedDataRepository로 맞추셨죠?
    private final RawCollectedDataRepository rawDataRepository;
    private final ParsedThreatDataRepository parsedDataRepository;
    private final DetectionKeywordRepository keywordRepository;
    private final AlertService alertService;

    public IngestionResponse processRawData(IngestionRequest request) {
        RawCollectedData raw = rawDataRepository.save(RawCollectedData.of(request.siteId(), request.rawText()));

        List<DetectionKeyword> activeKeywords = keywordRepository.findAllByActiveTrue();

        boolean isMatch = activeKeywords.stream()
                .anyMatch(k -> request.rawText().contains(k.getKeyword()));

        boolean alertSent = false;
        if (isMatch) {
            // [수정] 인자 5개로 맞춤 (rawId, indicatorValue, sourceName, title, content)
            ParsedThreatData parsed = parsedDataRepository.save(ParsedThreatData.create(
                    raw.getId(),
                    "extracted-value@mail.com", // 나중에 정규식으로 추출할 값
                    "Target-Site-" + request.siteId(), // 출처 정보
                    "Detected Leak: " + request.siteId(),
                    request.rawText()
            ));

            alertService.sendTelegramAlert("위협 감지! 키워드가 포함된 데이터가 수신되었습니다.");
            alertSent = true;
        }

        return new IngestionResponse(raw.getId(), alertSent);
    }

}