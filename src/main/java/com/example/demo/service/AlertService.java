package com.example.demo.service;

import com.example.demo.entity.SystemSetting;
import com.example.demo.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // 추가
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final SystemSettingRepository settingRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendTelegramAlert(String message) {
        SystemSetting setting = settingRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("알림 설정이 없습니다."));

        if (!setting.isAlertActive()) return;

        String token = setting.getTelegramBotToken();
        String chatId = setting.getTelegramChatId();

        // 텔레그램 API URL
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        // 파라미터 구성
        Map<String, Object> request = new HashMap<>();
        request.put("chat_id", chatId);
        request.put("text", message);

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println(" 텔레그램 API 호출 성공");
        } catch (Exception e) {
            System.err.println(" 텔레그램 발송 실패: " + e.getMessage());
        }
    }
}