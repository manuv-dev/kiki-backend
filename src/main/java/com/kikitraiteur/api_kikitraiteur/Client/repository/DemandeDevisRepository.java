package com.kikitraiteur.api_kikitraiteur.Client.repository;

import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeDevisRepository extends JpaRepository<DemandeDevis, Long> {

    /** Par client_id direct (via relation client) */
    @Query("SELECT d FROM DemandeDevis d WHERE d.client.id = :clientId ORDER BY d.dateSubmitted DESC")
    List<DemandeDevis> findAllByClientId(@Param("clientId") Long clientId);

    /** Par email du client */
    @Query("SELECT d FROM DemandeDevis d WHERE d.client.email = :email ORDER BY d.dateSubmitted DESC")
    List<DemandeDevis> findByClientEmail(@Param("email") String email);

    /** Toutes les demandes triées par date */
    List<DemandeDevis> findAllByOrderByDateSubmittedDesc();

    /** Par statut */
    List<DemandeDevis> findByStatusOrderByDateSubmittedDesc(String status);

    /** Demandes pendantes */
    @Query("SELECT d FROM DemandeDevis d WHERE d.status IN ('pending', 'propositions_envoyees', 'selection_client') ORDER BY d.dateSubmitted DESC")
    List<DemandeDevis> findActive();

    /** Count par statut */
    long countByStatus(String status);
}

