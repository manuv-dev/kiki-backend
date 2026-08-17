package com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionEnvoyee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropositionEnvoyeeRepository extends JpaRepository<PropositionEnvoyee, Long> {
    List<PropositionEnvoyee> findByDemandeId(Long demandeId);
    List<PropositionEnvoyee> findByDemandeIdOrderByDateEnvoiDesc(Long demandeId);
    /** Pour MyKiki : récupère toutes les propositions envoyées pour une liste de demandes */
    List<PropositionEnvoyee> findByDemandeIdInOrderByDateEnvoiDesc(List<Long> demandeIds);
    /** Alias court pour MyKikiController */
    default List<PropositionEnvoyee> findByDemandeIdIn(List<Long> ids) {
        return findByDemandeIdInOrderByDateEnvoiDesc(ids);
    }
    List<PropositionEnvoyee> findByStatusOrderByDateEnvoiDesc(String status);
}

