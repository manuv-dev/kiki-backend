package com.kikitraiteur.api_kikitraiteur.Gestionnaire.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propositions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double prixUnitairePersonne;

    private String imageUrl;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "proposition_id")
    @Builder.Default
    private List<PropositionSection> sections = new ArrayList<>();
}
