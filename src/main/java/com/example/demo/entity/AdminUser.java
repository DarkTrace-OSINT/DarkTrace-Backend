package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUser extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String googleId;

    private String name;

    private String role = "ROLE_ANALYST"; // 기본 역할

    // [규칙 10] 정적 팩토리 메서드
    public static AdminUser createPendingUser(String email, String googleId, String name) {
        AdminUser user = new AdminUser();
        user.email = email;
        user.googleId = googleId;
        user.name = name;
        return user;
    }
}