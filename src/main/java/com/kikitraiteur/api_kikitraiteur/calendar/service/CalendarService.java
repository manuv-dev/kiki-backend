package com.kikitraiteur.api_kikitraiteur.calendar.service;

import com.kikitraiteur.api_kikitraiteur.calendar.model.CalendarEvent;
import com.kikitraiteur.api_kikitraiteur.calendar.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarEventRepository repository;
    private final GoogleCalendarService googleCalendarService;

    @Transactional
    public CalendarEvent createEvent(CalendarEvent event) {
        CalendarEvent saved = repository.save(event);
        // Synchroniser avec Google Calendar
        Optional<String> googleId = googleCalendarService.syncEventToGoogle(saved);
        googleId.ifPresent(id -> {
            saved.setGoogleEventId(id);
            repository.save(saved);
        });
        return saved;
    }

    @Transactional
    public CalendarEvent updateEvent(Long id, CalendarEvent updated) {
        CalendarEvent existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable : " + id));

        existing.setTitre(updated.getTitre());
        existing.setType(updated.getType());
        existing.setDateDebut(updated.getDateDebut());
        existing.setDateFin(updated.getDateFin());
        existing.setHeureDebut(updated.getHeureDebut());
        existing.setHeureFin(updated.getHeureFin());
        existing.setNombreConvives(updated.getNombreConvives());
        existing.setLieu(updated.getLieu());
        existing.setClientId(updated.getClientId());
        existing.setClientName(updated.getClientName());
        existing.setPersonnelIds(updated.getPersonnelIds());
        existing.setResponsable(updated.getResponsable());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());

        CalendarEvent saved = repository.save(existing);
        // Sync Google
        googleCalendarService.syncEventToGoogle(saved);
        return saved;
    }

    @Transactional
    public void deleteEvent(Long id) {
        CalendarEvent event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable : " + id));
        if (event.getGoogleEventId() != null) {
            googleCalendarService.deleteFromGoogle(event.getGoogleEventId());
        }
        repository.delete(event);
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getByMonthAndYear(int year, int month) {
        return repository.findByMonthAndYear(year, month);
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getByDateRange(LocalDate start, LocalDate end) {
        return repository.findByDateDebutBetweenOrderByDateDebutAsc(start, end);
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getUpcoming() {
        return repository.findUpcoming(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> search(String query) {
        return repository.searchByQuery(query);
    }

    @Transactional(readOnly = true)
    public List<CalendarEvent> getByClient(Long clientId) {
        return repository.findByClientIdOrderByDateDebutDesc(clientId);
    }

    @Transactional(readOnly = true)
    public Optional<CalendarEvent> getById(Long id) {
        return repository.findById(id);
    }

    /** Crée automatiquement un événement depuis une demande aboutie */
    @Transactional
    public CalendarEvent createFromDemande(Long demandeId, String titre, String clientName,
                                           Long clientId, String lieu, LocalDate date,
                                           Integer guests, String type) {
        CalendarEvent event = CalendarEvent.builder()
                .titre(titre)
                .type(type)
                .dateDebut(date)
                .clientId(clientId)
                .clientName(clientName)
                .lieu(lieu)
                .nombreConvives(guests)
                .demandeId(demandeId)
                .status("confirme")
                .build();
        return createEvent(event);
    }
}
