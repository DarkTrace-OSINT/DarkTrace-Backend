package com.example.demo.global.utils;

import com.example.demo.entity.TargetSite;
import com.example.demo.entity.ThreatIndicator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexParser {

    private static final String EMAIL_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";
    private static final String IP_REGEX = "(\\d{1,3}\\.){3}\\d{1,3}";
    private static final String DOMAIN_REGEX = "(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}";

    /**
     * [수정] Long siteId 대신 TargetSite site 객체를 받도록 변경
     */
    public List<ThreatIndicator> parseIndicators(TargetSite site, String content) {
        List<ThreatIndicator> indicators = new ArrayList<>();

        // 이메일 추출 - siteId 대신 위에서 받은 site 객체를 넣어줍니다.
        extract(content, EMAIL_REGEX, "EMAIL").forEach(value ->
                indicators.add(ThreatIndicator.of(site, "EMAIL", value))
        );

        // IP 추출
        extract(content, IP_REGEX, "IP").forEach(value ->
                indicators.add(ThreatIndicator.of(site, "IP", value))
        );

        // 도메인 추출
        extract(content, DOMAIN_REGEX, "DOMAIN").forEach(value ->
                indicators.add(ThreatIndicator.of(site, "DOMAIN", value))
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
            if (!matches.contains(match)) {
                matches.add(match);
            }
        }
        return matches;
    }
}