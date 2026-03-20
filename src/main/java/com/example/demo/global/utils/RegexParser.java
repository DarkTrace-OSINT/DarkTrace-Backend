package com.example.demo.global.utils;

import com.example.demo.entity.ThreatIndicator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexParser {

    // 1. 이메일 정규식
    private static final String EMAIL_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";
    // 2. IPv4 정규식
    private static final String IP_REGEX = "(\\d{1,3}\\.){3}\\d{1,3}";
    // 3. 도메인/URL 정규식 (간이 버전)
    private static final String DOMAIN_REGEX = "(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}";

    /**
     * 원본 텍스트에서 모든 위협 지표를 추출하여 리스트로 반환
     */
    public List<ThreatIndicator> parseIndicators(Long siteId, String content) {
        List<ThreatIndicator> indicators = new ArrayList<>();

        // 이메일 추출
        extract(content, EMAIL_REGEX, "EMAIL").forEach(value ->
                indicators.add(ThreatIndicator.of(siteId, "EMAIL", value))
        );

        // IP 추출
        extract(content, IP_REGEX, "IP").forEach(value ->
                indicators.add(ThreatIndicator.of(siteId, "IP", value))
        );

        // 도메인 추출
        extract(content, DOMAIN_REGEX, "DOMAIN").forEach(value ->
                indicators.add(ThreatIndicator.of(siteId, "DOMAIN", value))
        );

        return indicators;
    }

    private List<String> extract(String content, String regex, String type) {
        List<String> matches = new ArrayList<>();
        if (content == null || content.isEmpty()) return matches;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String match = matcher.group();
            // 중복 방지 로직을 추가하면 더 좋습니다.
            if (!matches.contains(match)) {
                matches.add(match);
            }
        }
        return matches;
    }
}