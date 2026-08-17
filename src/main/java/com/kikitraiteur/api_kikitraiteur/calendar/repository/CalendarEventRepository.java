package com.kikitraiteur.api_kikitraiteur.calendar.repository;

import com.kikitraiteur.api_kikitraiteur.calendar.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /** Événements d'un mois/année spécifique */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "YEAR(e.dateDebut) = :year AND MONTH(e.dateDebut) = :month " +
           "ORDER BY e.dateDebut ASC, e.heureDebut ASC")
    List<CalendarEvent> findByMonthAndYear(@Param("year") int year, @Param("month") int month);

    /** Événements entre deux dates */
    List<CalendarEvent> findByDateDebutBetweenOrderByDateDebutAsc(LocalDate start, LocalDate end);

    /** Événements d'un client */
    List<CalendarEvent> findByClientIdOrderByDateDebutDesc(Long clientId);

    /** Événements d'un responsable */
    List<CalendarEvent> findByResponsableContainingIgnoreCaseOrderByDateDebutDesc(String responsable);

    /** Événements par type de prestation */
    List<CalendarEvent> findByTypeOrderByDateDebutDesc(String type);

    /** Événements à venir (non annulés) */
    @Query("SELECT e FROM CalendarEvent e WHERE e.dateDebut >= :today AND e.status != 'annule' ORDER BY e.dateDebut ASC")
    List<CalendarEvent> findUpcoming(@Param("today") LocalDate today);

    /** Événements liés à une demande */
    List<CalendarEvent> findByDemandeId(Long demandeId);

    /** Recherche globale par titre ou client */
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "LOWER(e.titre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(e.clientName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(e.responsable) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY e.dateDebut DESC")
    List<CalendarEvent> searchByQuery(@Param("q") String query);
}
