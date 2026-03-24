package com.example.demo.repository;

import com.example.demo.entity.RawCollectedData; // 👈 엔티티 경로 확인!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawCollectedDataRepository extends JpaRepository<RawCollectedData, Long> {
    // 깡통으로 둬도 JpaRepository가 save() 메서드를 자동으로 만들어줌
}