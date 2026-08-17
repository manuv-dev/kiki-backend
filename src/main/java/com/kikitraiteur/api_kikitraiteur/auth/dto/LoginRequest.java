package com.kikitraiteur.api_kikitraiteur.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    /** Optionnel : slug personnalisé pour accès admin/gestionnaire */
    private String slug;
}
