package com.kikitraiteur.api_kikitraiteur.auth.dto;

import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private UserRole role;
    private boolean tempPasswordChangeRequired;
    private boolean active;
    private String customLoginSlug;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
