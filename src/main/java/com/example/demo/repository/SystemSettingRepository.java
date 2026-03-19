package com.example.demo.repository; // 패키지 경로 확인!

import com.example.demo.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    // [수정] settingId 대신 id를 기준으로 정렬하도록 명칭 변경
    Optional<SystemSetting> findFirstByOrderByIdAsc();
}