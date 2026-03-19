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
    private Long siteId;
    private String indicatorType;
    private String indicatorValue;

    public static ThreatIndicator of(Long siteId, String type, String value) {
        ThreatIndicator indicator = new ThreatIndicator();
        indicator.siteId = siteId;
        indicator.indicatorType = type;
        indicator.indicatorValue = value;
        return indicator;
    }
}