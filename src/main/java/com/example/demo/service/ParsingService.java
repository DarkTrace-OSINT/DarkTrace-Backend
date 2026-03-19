package com.example.demo.service;

import com.example.demo.dto.request.IngestionRequest;
import com.example.demo.dto.response.IngestionResponse;
import com.example.demo.entity.*; // 모든 엔티티 임포트
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
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
        // 1. RawData 대신 RawCollectedData.of() 사용 (사진 21 에러 해결)
        RawCollectedData raw = rawDataRepository.save(RawCollectedData.of(request.siteId(), request.rawText()));

        // 2. findAllByIsActiveTrue 대신 findAllByActiveTrue() 사용 (사진 21 에러 해결)
        List<DetectionKeyword> activeKeywords = keywordRepository.findAllByActiveTrue();

        boolean isMatch = activeKeywords.stream()
                .anyMatch(k -> request.rawText().contains(k.getKeyword()));

        boolean alertSent = false;
        if (isMatch) {
            // 3. new 대신 .create() 정적 메서드 사용 (사진 21 에러 해결)
            ParsedThreatData parsed = parsedDataRepository.save(ParsedThreatData.create(
                    raw.getId(),
                    "🚨 Detected Leak: " + request.siteId(),
                    request.rawText()
            ));

            // 4. 알림 발송
            alertService.sendTelegramAlert("위협 감지! 키워드가 포함된 데이터가 수신되었습니다.");
            alertSent = true;
        }

        return new IngestionResponse(raw.getId(), alertSent);
    }
}