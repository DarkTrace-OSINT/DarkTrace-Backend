package com.example.demo.repository;

import com.example.demo.entity.DetectionKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetectionKeywordRepository extends JpaRepository<DetectionKeyword, Long> {

    /**
     * [API 7] 활성화 상태(is_active = true)인 키워드들만
     * 조회할 때 사용
     */

    List<DetectionKeyword> findAllByActiveTrue();
}