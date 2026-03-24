package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "threat_indicators")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThreatIndicator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 숫자 대신 TargetSite 객체와 연관관계를 맺음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private TargetSite site;

    private String indicatorType;
    private String indicatorValue;

    // 생성 메서드도 숫자가 아닌 객체를 받도록 수정
    public static ThreatIndicator of(TargetSite site, String type, String value) {
        ThreatIndicator indicator = new ThreatIndicator();
        indicator.site = site;
        indicator.indicatorType = type;
        indicator.indicatorValue = value;
        return indicator;
    }
}