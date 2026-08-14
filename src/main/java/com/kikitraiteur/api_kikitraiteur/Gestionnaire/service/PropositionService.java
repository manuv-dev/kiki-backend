package com.kikitraiteur.api_kikitraiteur.Gestionnaire.service;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.Proposition;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionSection;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionSectionItem;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository.PropositionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropositionService {
    
    private final PropositionRepository propositionRepository;

    @PostConstruct
    public void initDefaultPropositions() {
        if (propositionRepository.count() == 0) {
            log.info("Création de 2 propositions par défaut pour le catalogue...");
            
            Proposition prop1 = Proposition.builder()
                .titre("Proposition de Menu Déjeuner")
                .description("Un menu complet pour vos déjeuners d'entreprise.")
                .prixUnitairePersonne(15000.0)
                .sections(List.of(
                    PropositionSection.builder().nom("Entrées").maxChoix(1).items(List.of(
                        PropositionSectionItem.builder().nom("Salade Niçoise").description("Fraîcheur de saison").build(),
                        PropositionSectionItem.builder().nom("Nems au poulet").description("Croustillants et savoureux").build()
                    )).build(),
                    PropositionSection.builder().nom("Plats Chauds").maxChoix(1).items(List.of(
                        PropositionSectionItem.builder().nom("Thiep Bou Dien").description("Plat national").build(),
                        PropositionSectionItem.builder().nom("Filet de boeuf").description("Sauce poivre vert").build()
                    )).build(),
                    PropositionSection.builder().nom("Desserts").maxChoix(2).items(List.of(
                        PropositionSectionItem.builder().nom("Tarte au citron").description("Meringuée").build(),
                        PropositionSectionItem.builder().nom("Fondant au chocolat").description("Coeur coulant").build(),
                        PropositionSectionItem.builder().nom("Salade de fruits").description("Fruits de saison").build()
                    )).build()
                ))
                .build();
                
            Proposition prop2 = Proposition.builder()
                .titre("Formule Cocktail Corporate")
                .description("Idéal pour vos séminaires et rencontres d'affaires.")
                .prixUnitairePersonne(10000.0)
                .sections(List.of(
                    PropositionSection.builder().nom("Pièces Salées").maxChoix(4).items(List.of(
                        PropositionSectionItem.builder().nom("Verrines fraîcheur").description("Avocat crevettes").build(),
                        PropositionSectionItem.builder().nom("Brochettes de viande").description("Poulet mariné").build(),
                        PropositionSectionItem.builder().nom("Mini burgers").description("Boeuf et cheddar").build(),
                        PropositionSectionItem.builder().nom("Croustillants de chèvre").description("Miel et thym").build(),
                        PropositionSectionItem.builder().nom("Tartare de saumon").description("Sur blinis").build()
                    )).build(),
                    PropositionSection.builder().nom("Pièces Sucrées").maxChoix(2).items(List.of(
                        PropositionSectionItem.builder().nom("Mini choux").description("Crème vanille").build(),
                        PropositionSectionItem.builder().nom("Macarons").description("Assortiment").build(),
                        PropositionSectionItem.builder().nom("Verrines fruits rouges").description("Mousse légère").build()
                    )).build()
                ))
                .build();
                
            propositionRepository.saveAll(List.of(prop1, prop2));
            log.info("Propositions par défaut créées avec succès.");
        }
    }

    @Transactional(readOnly = true)
    public List<Proposition> getAllPropositions() {
        return propositionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Proposition getPropositionById(Long id) {
        return propositionRepository.findById(id).orElseThrow(() -> new RuntimeException("Proposition non trouvée : " + id));
    }

    @Transactional
    public Proposition createProposition(Proposition proposition) {
        return propositionRepository.save(proposition);
    }

    @Transactional
    public Proposition updateProposition(Long id, Proposition updated) {
        Proposition prop = getPropositionById(id);
        prop.setTitre(updated.getTitre());
        prop.setDescription(updated.getDescription());
        prop.setPrixUnitairePersonne(updated.getPrixUnitairePersonne());
        prop.setImageUrl(updated.getImageUrl());
        
        prop.getSections().clear();
        if (updated.getSections() != null) {
            prop.getSections().addAll(updated.getSections());
        }
        
        return propositionRepository.save(prop);
    }
    
    @Transactional
    public void deleteProposition(Long id) {
        propositionRepository.deleteById(id);
    }
}
