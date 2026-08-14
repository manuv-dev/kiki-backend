package com.kikitraiteur.api_kikitraiteur.Gestionnaire.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proposition_section_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionSectionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
