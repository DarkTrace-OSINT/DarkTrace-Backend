package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // 1. Setter 추가

@Entity
@Table(name = "detection_keywords")
@Getter
@Setter // 2. 여기에 Setter를 붙여야 서비스에서 setActive()를 쓸 수 있습니다!
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetectionKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @Column(name = "is_active")
    private boolean active = true;

    public void setActive(boolean active) {
        this.active = active;
    }

    public static DetectionKeyword of(String keyword) {
        DetectionKeyword dk = new DetectionKeyword();
        dk.keyword = keyword;
        dk.active = true;
        return dk;
    }
}