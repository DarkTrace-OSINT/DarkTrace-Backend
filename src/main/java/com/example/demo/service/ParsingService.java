package com.example.demo.service;

import com.example.demo.dto.request.IngestionRequest;
import com.example.demo.dto.response.IngestionResponse;
import com.example.demo.entity.*;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.repository.*;
import com.example.demo.global.exception.DataParsingException;
import com.example.demo.global.utils.RegexParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    private static final Pattern CREDENTIAL_PATTERN =
            Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|[a-zA-Z0-9._-]+)[:|\\s]([a-zA-Z0-9!@#$%^&*()_+=-]+)");

    public IngestionResponse processRawData(IngestionRequest request) {
        log.info("[API 8번] 데이터 수신 시작 - SiteId: {}, TextLength: {}", request.siteId(), request.rawText().length());

        // 1. 원본 저장 (이게 되어야 200 OK의 의미가 있음)
        RawCollectedData raw = rawDataRepository.save(RawCollectedData.of(request.siteId(), request.rawText()));
        log.info("1. 원본 데이터 저장 완료 - RawId: {}", raw.getId());

        // [전처리] HTML 태그 제거 (정규식 추출 성능 향상)
        String plainText = request.rawText().replaceAll("<[^>]*>", " ");

        // 2. ThreatIndicator(이메일, IP 등) 추출 및 저장
        TargetSite site = siteRepository.findById(request.siteId())
                .orElseThrow(() -> new RuntimeException("사이트 정보가 없습니다. ID: " + request.siteId()));

        List<ThreatIndicator> indicators = regexParser.parseIndicators(site, plainText);
        log.info("2. 지표 추출 결과 - 개수: {}건", indicators.size());

        if (!indicators.isEmpty()) {
            indicatorRepository.saveAll(indicators);
            log.info("   -> threat_indicators 테이블 저장 성공");
        } else {
            log.warn("   -> 추출된 지표가 없습니다. 본문을 확인하세요.");
        }

        // 3. 실시간 키워드 매칭 및 알림
        List<DetectionKeyword> activeKeywords = keywordRepository.findAllByActiveTrue();
        boolean isMatch = activeKeywords.stream()
                .anyMatch(k -> plainText.contains(k.getKeyword()));

        boolean alertSent = false;
        if (isMatch) {
            log.info("3. 키워드 매칭 성공! 계정 정보 추출 시도...");
            List<String> credentials = extractAndValidate(plainText);

            if (!credentials.isEmpty()) {
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
                log.info("   -> parsed_threat_data 테이블 저장 완료: {}건", parsedList.size());

                alertService.sendTelegramAlert("위협 감지 " + site.getSourceName() + "에서 " + credentials.size() + "건의 정보 유출");
                alertSent = true;
            }
        }

        return new IngestionResponse(raw.getId(), alertSent);
    }

    private List<String> extractAndValidate(String text) {
        List<String> results = new ArrayList<>();
        Matcher matcher = CREDENTIAL_PATTERN.matcher(text);
        while (matcher.find()) {
            String identity = matcher.group(1).trim();
            String password = matcher.group(2).trim();
            if (identity.length() > 3 && password.length() > 0) {
                results.add(identity + ":" + password);
            }
        }
        return results;
    }
}