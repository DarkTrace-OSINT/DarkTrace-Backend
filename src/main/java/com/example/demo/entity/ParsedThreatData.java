package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*; // Setter 추가를 위해 변경
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "parsed_threat_data")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ParsedThreatData extends BaseEntity {

    @Column(name = "raw_id")
    private Long rawId;

    @Column(name = "indicator_value", length = 500)
    private String indicatorValue;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "title")
    private String title;

    @Column(name = "leak_title")
    private String leakTitle;

    @Column(name = "leak_content", columnDefinition = "TEXT")
    private String leakContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "indicator_type")
    private IndicatorType indicatorType = IndicatorType.EMAIL; // 기본값

    @Enumerated(EnumType.STRING)
    @Column(name = "action_status")
    private ActionStatus actionStatus = ActionStatus.OPEN; // 기본값

    @Column(name = "action_note")
    private String actionNote;

    /**
     * 정적 팩토리 메서드 업데이트
     */
    public static ParsedThreatData create(Long rawId, String indicatorValue, String sourceName, String title, String content) {
        ParsedThreatData data = new ParsedThreatData();
        data.rawId = rawId;
        data.indicatorValue = indicatorValue;
        data.sourceName = sourceName;
        data.title = title;
        data.leakTitle = title;
        data.leakContent = content;
        data.indicatorType = IndicatorType.EMAIL; // 엔진에서 타입을 받아오면 수정 가능
        data.actionStatus = ActionStatus.OPEN;
        return data;
    }
}