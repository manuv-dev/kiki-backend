package com.kikitraiteur.api_kikitraiteur.Client.repository;

import com.kikitraiteur.api_kikitraiteur.Client.model.Devis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {
    Optional<Devis> findByDemandeId(Long demandeId);
    List<Devis> findAllByOrderByIdDesc();
}
