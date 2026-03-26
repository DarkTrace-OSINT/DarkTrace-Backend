package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "raw_collected_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawCollectedData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long siteId;

    @Column(name = "raw_content", columnDefinition = "TEXT")
    private String rawContent;

    // 정적 팩토리 메서드 (매개변수명과 필드명이 같으니 this나 data로 확실히 구분)
    public static RawCollectedData of(Long siteId, String content) {
        RawCollectedData data = new RawCollectedData();
        data.siteId = siteId;
        data.rawContent = content;
        return data;
    }
}