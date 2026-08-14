package com.kikitraiteur.api_kikitraiteur.Gestionnaire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proposition_envoyee_section_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionEnvoyeeSectionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    // To track if the client has selected this item
    @Builder.Default
    private Boolean isSelected = false;
}
