package com.kikitraiteur.api_kikitraiteur.Gestionnaire.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propositions_envoyees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionEnvoyee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long demandeId;

    @Column(nullable = false)
    private Long propositionId;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double prixUnitairePersonne;

    /**
     * Statuts :
     * "envoyee"               → envoyée au client
     * "vue"                   → le client l'a consultée
     * "selectionnee_client"   → le client l'a sélectionnée (avec ou sans modifications)
     * "validee_gestionnaire"  → le gestionnaire a confirmé → demande aboutie
     * "rejetee"               → non retenue
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "envoyee";

    /** Commentaire laissé par le client lors de sa sélection */
    @Column(name = "client_comment", columnDefinition = "TEXT")
    private String clientComment;

    /** Sélection JSON du client (plats/options choisis) */
    @Column(name = "client_selection", columnDefinition = "TEXT")
    private String clientSelection;

    /** Date à laquelle le client a validé */
    private LocalDateTime clientValidatedAt;

    /** Date à laquelle le gestionnaire a confirmé → rend la demande "aboutie" */
    private LocalDateTime gestionnaireValidatedAt;

    private LocalDateTime dateEnvoi;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "proposition_envoyee_id")
    @Builder.Default
    private List<PropositionEnvoyeeSection> sections = new ArrayList<>();

    @PrePersist
    protected void onPrePersist() {
        this.dateEnvoi = LocalDateTime.now();
        if (this.status == null) {
            this.status = "envoyee";
        }
    }
}
