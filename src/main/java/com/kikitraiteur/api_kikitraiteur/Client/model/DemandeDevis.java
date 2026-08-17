package com.kikitraiteur.api_kikitraiteur.Client.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demandes_devis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeDevis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "client_type")
    private String clientType;

    private String organization;

    @Column(nullable = false)
    private String prestationId;

    private String evenementNature;

    @Column(name = "event_date")
    private String date;

    @Column(name = "event_time")
    private String time;

    private Integer guests;

    private String locationType;

    private String locationDetails;

    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * Statuts possibles :
     * "pending"              → en attente de traitement
     * "propositions_envoyees"→ le gestionnaire a envoyé des propositions
     * "selection_client"     → le client a fait sa sélection
     * "aboutis"              → demande conclue, événement créé
     * "rejected"             → demande refusée
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "pending";

    /** Motif de refus (optionnel) */
    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    /** Historique des propositions envoyées (JSON) — pour l'itération */
    @Column(name = "historique_propositions", columnDefinition = "TEXT")
    private String historiquePropositions;

    @Column(updatable = false)
    private LocalDateTime dateSubmitted;

    /** ID de l'événement calendrier créé lors du passage en "aboutis" */
    @Column(name = "calendar_event_id")
    private Long calendarEventId;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = "pending";
        }
        this.dateSubmitted = LocalDateTime.now();
    }
}
