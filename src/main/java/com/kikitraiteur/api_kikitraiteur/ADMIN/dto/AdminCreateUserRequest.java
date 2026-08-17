package com.kikitraiteur.api_kikitraiteur.ADMIN.dto;

import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import lombok.Data;

@Data
public class AdminCreateUserRequest {
    private String fullName;
    private String username;
    private UserRole role; // GESTIONNAIRE or PERSONNEL
}
