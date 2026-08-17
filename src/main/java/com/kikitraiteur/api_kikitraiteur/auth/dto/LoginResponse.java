package com.kikitraiteur.api_kikitraiteur.auth.dto;

import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String fullName;
    private UserRole role;
    private boolean tempPasswordChangeRequired;
    /** URL de redirection suggérée selon le rôle */
    private String redirectUrl;
}
