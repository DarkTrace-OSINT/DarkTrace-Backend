package com.example.demo.entity;

import jakarta.persistence.*; // 필수 Import 추가!
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parsed_threat_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParsedThreatData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rawId; // 원본 Raw 데이터와의 연결 고리

    private String leakTitle; // 유출 제목 (예: "XX 사이트 계정 유출")

    @Column(columnDefinition = "TEXT")
    private String leakContent; // 유출 내용 (텍스트가 길 수 있으니 TEXT 타입 유지)

    public static ParsedThreatData create(Long rawId, String title, String content) {
        ParsedThreatData data = new ParsedThreatData();
        data.rawId = rawId;
        data.leakTitle = title;
        data.leakContent = content;
        return data;
    }
}