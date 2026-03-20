package com.example.demo.entity;

import jakarta.persistence.*; // 필수 Import 추가!
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

    private Long rawId;
    private String indicatorValue;
    private String sourceName;
    private String leakTitle;

    @Column(columnDefinition = "TEXT")
    private String leakContent;

    public static ParsedThreatData create(Long rawId, String indicatorValue, String sourceName, String title, String content) {
        ParsedThreatData data = new ParsedThreatData();
        data.rawId = rawId;
        data.indicatorValue = indicatorValue;
        data.sourceName = sourceName;
        data.leakTitle = title;
        data.leakContent = content;
        return data;
    }
}