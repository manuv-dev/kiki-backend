package com.kikitraiteur.api_kikitraiteur.ADMIN.dto;

import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String fullName;
    private UserRole role;
    private String customLoginSlug;
    private boolean active;
    private boolean tempPasswordChangeRequired;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    // Retourné uniquement lors de la création ou d'un reset d'accès
    private String tempPassword;
    private String loginUrl;
}
