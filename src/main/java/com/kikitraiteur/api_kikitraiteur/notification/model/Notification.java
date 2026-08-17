package com.kikitraiteur.api_kikitraiteur.notification.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type de notification :
     * NOUVELLE_DEMANDE, PROPOSITION_ENVOYEE, PROPOSITION_VALIDEE_CLIENT,
     * PROPOSITION_VALIDEE_GESTIONNAIRE, DEMANDE_ABOUTIE
     */
    @Column(nullable = false)
    private String type;

    /**
     * Rôle cible (ADMIN, GESTIONNAIRE, CLIENT).
     * Null si targetUserId est précisé.
     */
    private String targetRole;

    /**
     * ID de l'AppUser destinataire (null si targetRole est utilisé).
     */
    private Long targetUserId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /** Référence à la demande concernée (nullable) */
    private Long demandeId;

    /** Référence à la proposition envoyée concernée (nullable) */
    private Long propositionEnvoyeeId;

    @Column(nullable = false)
    @Builder.Default
    private boolean lu = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
