package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_settings")
@Getter
@NoArgsConstructor
public class SystemSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramBotToken;
    private String telegramChatId;
    private boolean isAlertActive = true;

    // [추가] 서비스에서 호출하는 업데이트 로직
    public void updateConfig(String token, String chatId) {
        this.telegramBotToken = token;
        this.telegramChatId = chatId;
    }
}