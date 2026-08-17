package com.kikitraiteur.api_kikitraiteur.auth.dto;

import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String fullName;
    private String username;
    /** Mot de passe temporaire (optionnel — généré si absent) */
    private String temporaryPassword;
    private UserRole role;
    /** Slug URL personnalisé (optionnel — généré si absent pour ADMIN/GESTIONNAIRE) */
    private String customLoginSlug;
    /** Spécialité/poste pour les personnels */
    private String specialite;
}
