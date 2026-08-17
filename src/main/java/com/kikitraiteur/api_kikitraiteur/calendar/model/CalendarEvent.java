package com.kikitraiteur.api_kikitraiteur.calendar.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "calendar_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    /** Type de prestation (traiteur, evenements, salle-diva, etc.) */
    private String type;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    private LocalTime heureDebut;

    private LocalTime heureFin;

    private Integer nombreConvives;

    /** Lieu de l'événement */
    @Column(columnDefinition = "TEXT")
    private String lieu;

    private Long clientId;

    private String clientName;

    /** IDs des personnels affectés (JSON array string) */
    @Column(name = "personnel_ids", columnDefinition = "TEXT")
    private String personnelIds;

    private String responsable;

    /**
     * Statut : "planifie", "confirme", "en_cours", "termine", "annule"
     */
    @Builder.Default
    private String status = "planifie";

    /** ID de la demande d'origine (si créé depuis une demande aboutie) */
    private Long demandeId;

    /** ID de l'événement Google Calendar (null si non synchronisé) */
    @Column(name = "google_event_id")
    private String googleEventId;

    /** Notes internes */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = "planifie";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
