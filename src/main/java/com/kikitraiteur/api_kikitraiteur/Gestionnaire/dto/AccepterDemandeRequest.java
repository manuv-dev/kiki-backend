package com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto;

import lombok.Data;
import java.util.List;

/** Payload pour accepter une demande et lui envoyer des propositions */
@Data
public class AccepterDemandeRequest {
    /** IDs des propositions du catalogue à envoyer */
    private List<Long> propositionIds;
    /** Optionnel : créer une nouvelle proposition inline */
    private NouvellePropositionInline nouvelleProposition;

    @Data
    public static class NouvellePropositionInline {
        private String titre;
        private String description;
        private Double prixUnitairePersonne;
        private String imageUrl;
    }
}
