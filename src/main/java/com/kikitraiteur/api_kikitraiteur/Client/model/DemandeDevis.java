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
