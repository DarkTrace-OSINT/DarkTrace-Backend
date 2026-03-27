package com.example.demo.service;

import com.example.demo.entity.SystemSetting;
import com.example.demo.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final SystemSettingRepository settingRepository;

//    public void sendTelegramAlert(String message) {
//        // [수정] settingId -> id로 변경 (엔티티 필드명과 일치)
//        SystemSetting setting = settingRepository.findFirstByOrderByIdAsc()
//                .orElseThrow(() -> new RuntimeException("알림 설정이 없습니다."));
//
//        if (!setting.isAlertActive()) return;
//
//        // 실제 전송 로직
//        System.out.println("텔레그램 발송: " + message);
//    }
}