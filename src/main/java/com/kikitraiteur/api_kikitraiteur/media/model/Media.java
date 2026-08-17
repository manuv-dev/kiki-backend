package com.kikitraiteur.api_kikitraiteur.media.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    /** URL relative ou absolue du fichier */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    /** Type : IMAGE, VIDEO, PDF, DOCUMENT */
    @Column(nullable = false)
    @Builder.Default
    private String type = "IMAGE";

    /** Taille en octets */
    private Long sizeBytes;

    /** ID de l'événement associé (nullable — médiathèque libre si null) */
    private Long evenementId;

    /** Nom de l'événement (dénormalisé pour affichage rapide) */
    private String evenementNom;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
        if (this.type == null) this.type = "IMAGE";
    }
}
