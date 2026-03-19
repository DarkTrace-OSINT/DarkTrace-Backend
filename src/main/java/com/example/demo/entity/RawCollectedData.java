package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "raw_collected_data") // 테이블 명칭도 클래스명과 맞췄습니다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawCollectedData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long siteId;

    @Column(columnDefinition = "TEXT")
    private String rawContent;

    // [규칙 10] 정적 팩토리 메서드: RawCollectedData 타입 반환
    public static RawCollectedData of(Long siteId, String rawContent) {
        RawCollectedData data = new RawCollectedData();
        data.siteId = siteId;
        data.rawContent = rawContent;
        return data;
    }
}