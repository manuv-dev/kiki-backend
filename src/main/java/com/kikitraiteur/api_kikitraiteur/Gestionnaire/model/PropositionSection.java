package com.kikitraiteur.api_kikitraiteur.Gestionnaire.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proposition_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private Integer maxChoix;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id")
    @Builder.Default
    private List<PropositionSectionItem> items = new ArrayList<>();
}
