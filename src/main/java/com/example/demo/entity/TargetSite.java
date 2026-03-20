package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "target_sites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TargetSite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceName;
    private String url;
    private String crawlerStatus; // ALIVE, DEAD, ERROR
    private LocalDateTime lastCrawledAt;

    public static TargetSite createSite(String sourceName, String url) {
        TargetSite site = new TargetSite();
        site.sourceName = sourceName;
        site.url = url;
        site.crawlerStatus = "ALIVE";
        return site;
    }
}