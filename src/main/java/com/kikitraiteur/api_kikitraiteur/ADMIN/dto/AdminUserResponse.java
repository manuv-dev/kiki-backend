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
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    // Used when returning a newly created user to show the auto-generated password
    private String tempPassword;
    private String loginUrl;
}
