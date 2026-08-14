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

    @Column(nullable = false)
    private String status; 

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
