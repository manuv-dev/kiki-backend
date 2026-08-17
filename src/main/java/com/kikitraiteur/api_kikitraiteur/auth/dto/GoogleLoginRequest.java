package com.kikitraiteur.api_kikitraiteur.auth.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String credential; // The ID token from Google
}
