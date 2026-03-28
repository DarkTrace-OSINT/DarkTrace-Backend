package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SystemSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_bot_token")
    private String telegramBotToken;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Column(name = "is_alert_active", columnDefinition = "BIT(1)")
    @JsonProperty("isAlertEnabled")
    private boolean isAlertActive = true;

    public void updateConfig(String token, String chatId, boolean isEnabled) {
        this.telegramBotToken = token;
        this.telegramChatId = chatId;
        this.isAlertActive = isEnabled;
    }
}