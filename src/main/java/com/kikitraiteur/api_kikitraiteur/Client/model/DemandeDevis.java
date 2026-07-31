package com.kikitraiteur.api_kikitraiteur.Client.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String prestationId;

    private String prestationTitle;

    @Column(name = "event_date")
    private String date;

    @Column(name = "event_time")
    private String time;

    private Integer guests;

    private Boolean isInstitution;

    private String organization;

    private String location;

    private String cuisine;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String status; // "pending", "approved", "rejected"

    @Column(updatable = false)
    private LocalDateTime dateSubmitted;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = "pending";
        }
        this.dateSubmitted = LocalDateTime.now();
    }
}
