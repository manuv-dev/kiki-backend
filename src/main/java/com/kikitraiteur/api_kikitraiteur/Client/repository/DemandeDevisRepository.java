package com.kikitraiteur.api_kikitraiteur.Client.repository;

import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeDevisRepository extends JpaRepository<DemandeDevis, Long> {
    List<DemandeDevis> findByClientId(Long clientId);
    List<DemandeDevis> findByClientEmail(String email);
    List<DemandeDevis> findAllByOrderByDateSubmittedDesc();
}
