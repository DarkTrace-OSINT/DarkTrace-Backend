package com.example.demo.repository; // 패키지 경로 확인!

import com.example.demo.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findFirstByOrderByIdAsc();
}