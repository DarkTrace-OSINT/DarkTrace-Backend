package com.example.demo.entity;

import jakarta.persistence.*; // @Id, @GeneratedValue를 쓰기 위해 필요
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detection_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetectionKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @Column(name = "is_active")
    private boolean active = true;

    public static DetectionKeyword of(String keyword) {
        DetectionKeyword dk = new DetectionKeyword();
        dk.keyword = keyword;
        dk.active = true; // 생성 시 기본값 명시
        return dk;
    }
}