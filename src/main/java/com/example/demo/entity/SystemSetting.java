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

    public void updateConfig(String token, String chatId, boolean isEnabled) {
        this.telegramBotToken = token;
        this.telegramChatId = chatId;
        this.isAlertActive = isEnabled;
    }
}