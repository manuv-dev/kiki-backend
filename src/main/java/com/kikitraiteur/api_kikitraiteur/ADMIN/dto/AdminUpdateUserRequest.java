package com.kikitraiteur.api_kikitraiteur.ADMIN.dto;

import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import lombok.Data;

@Data
public class AdminUpdateUserRequest {
    private String fullName;
    private String username;
    private UserRole role;
    private boolean active;
}
