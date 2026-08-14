package com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionEnvoyee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropositionEnvoyeeRepository extends JpaRepository<PropositionEnvoyee, Long> {
    List<PropositionEnvoyee> findByDemandeId(Long demandeId);
}
