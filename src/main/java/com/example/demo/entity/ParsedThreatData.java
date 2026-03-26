package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "parsed_threat_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ParsedThreatData extends BaseEntity {

    @Column(name = "raw_id")
    private Long rawId;

    @Column(name = "indicator_value", length = 500) // URL이 길어질 수 있으니 500자 확보
    private String indicatorValue;
    @Column(name = "source_name")
    private String sourceName;
    @Column(name = "title")
    private String title;
    @Column(name = "leak_title")
    private String leakTitle;
    @Column(name = "leak_content", columnDefinition = "TEXT")
    private String leakContent;

    /**
     * 정적 팩토리 메서드: 엔진에서 받은 데이터를 객체로 변환
     */
    public static ParsedThreatData create(Long rawId, String indicatorValue, String sourceName, String title, String content) {
        ParsedThreatData data = new ParsedThreatData();
        data.rawId = rawId;
        data.indicatorValue = indicatorValue;
        data.sourceName = sourceName;
        data.title = title;      // 지수님 검색용 제목
        data.leakTitle = title;  // 기존 호환성용 제목
        data.leakContent = content;
        return data;
    }
}