package com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropositionRepository extends JpaRepository<Proposition, Long> {
}
