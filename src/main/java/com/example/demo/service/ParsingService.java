package com.example.demo.service;

import com.example.demo.dto.request.IngestionRequest;
import com.example.demo.dto.response.IngestionResponse;
import com.example.demo.entity.*;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.repository.*;
import com.example.demo.global.exception.DataParsingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.demo.global.utils.RegexParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParsingService {

    private final RawCollectedDataRepository rawDataRepository;
    private final ParsedThreatDataRepository parsedDataRepository;
    private final ThreatIndicatorRepository indicatorRepository;
    private final TargetSiteRepository siteRepository;
    private final DetectionKeywordRepository keywordRepository;
    private final AlertService alertService;
    private final RegexParser regexParser;

    // [정규식 고도화] 이메일/ID : PW 패턴
    private static final Pattern CREDENTIAL_PATTERN =
            Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|[a-zA-Z0-9._-]+)[:|\\s]([a-zA-Z0-9!@#$%^&*()_+=-]+)");

    public IngestionResponse processRawData(IngestionRequest request) {
        // 1. 원본 저장 및 유효성 검사
        RawCollectedData raw = rawDataRepository.save(RawCollectedData.of(request.siteId(), request.rawText()));

        // 2. [추가] ThreatIndicator(이메일, IP 등) 추출 및 저장
        TargetSite site = siteRepository.findById(request.siteId()).orElseThrow();
        List<ThreatIndicator> indicators = regexParser.parseIndicators(site, request.rawText());
        if (!indicators.isEmpty()) {
            indicatorRepository.saveAll(indicators); // 드디어 DB에 저장!
        }
        // 2. 실시간 키워드 매칭
        List<DetectionKeyword> activeKeywords = keywordRepository.findAllByActiveTrue();
        boolean isMatch = activeKeywords.stream()
                .anyMatch(k -> request.rawText().contains(k.getKeyword()));

        boolean alertSent = false;
        if (isMatch) {
            // 3. [로그 처리 및 대용량 대응]
            List<String> credentials = extractAndValidate(request.rawText());

            if (!credentials.isEmpty()) {
                // [성능 최적화] 리스트에 담아 한 번에 Batch 저장 (saveAll)
                List<ParsedThreatData> parsedList = new ArrayList<>();
                for (String cred : credentials) {
                    parsedList.add(ParsedThreatData.create(
                            raw.getId(),
                            cred,
                            "Site-ID-" + request.siteId(),
                            "유출 계정 탐지",
                            request.rawText()
                    ));
                }
                parsedDataRepository.saveAll(parsedList);

                alertService.sendTelegramAlert("위협 감지! 총 " + credentials.size() + "건의 유출 정보가 식별되었습니다.");
                alertSent = true;
            }
        }

        return new IngestionResponse(raw.getId(), alertSent);
    }

    /**
     * [데이터 정제 및 최종 검증]
     */
    private List<String> extractAndValidate(String text) {
        List<String> results = new ArrayList<>();
        Matcher matcher = CREDENTIAL_PATTERN.matcher(text);

        while (matcher.find()) {
            String identity = matcher.group(1).trim();
            String password = matcher.group(2).trim();

            // ID가 너무 짧거나 PW가 없으면 승인 거부
            if (identity.length() > 3 && password.length() > 0) {
                results.add(identity + ":" + password);
            }
        }

        // 만약 키워드는 맞는데 뽑을 데이터가 하나도 없다면 예외 로깅
        if (results.isEmpty()) {
            log.warn("키워드는 매칭되었으나 정규식 추출에 실패했습니다. 본문: {}", text);
        }

        return results;
    }
}