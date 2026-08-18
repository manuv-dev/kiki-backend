package com.kikitraiteur.api_kikitraiteur.auth.model;

public enum UserRole {
    // ─── Rôles d'accès principal ───
    ADMIN,
    GESTIONNAIRE,
    CLIENT,

    // ─── Rôle générique personnel (rétrocompatibilité) ───
    PERSONNEL,

    // ─── Sous-rôles personnel (équipe culinaire & logistique) ───
    RESPONSABLE_CUISINE,
    SOUS_CHEF,
    ECONOME,
    MAGASINIER,
    CONTROLEUR,
    CUISINIER,
    SERVEUR,
    AIDE_CUISINIER,
    CHAUFFEUR,
    PLONGEUR,
    AGENT_SECURITE
}

