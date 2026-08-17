package com.kikitraiteur.api_kikitraiteur.calendar.controller;

import com.kikitraiteur.api_kikitraiteur.calendar.model.CalendarEvent;
import com.kikitraiteur.api_kikitraiteur.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestionnaire/calendar")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'PERSONNEL')")
public class CalendarController {

    private final CalendarService calendarService;

    /** Événements d'un mois/année */
    @GetMapping("/events")
    public ResponseEntity<List<CalendarEvent>> getEvents(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false, defaultValue = "false") boolean upcoming
    ) {
        List<CalendarEvent> events;

        if (q != null && !q.isBlank()) {
            events = calendarService.search(q);
        } else if (clientId != null) {
            events = calendarService.getByClient(clientId);
        } else if (start != null && end != null) {
            events = calendarService.getByDateRange(start, end);
        } else if (upcoming) {
            events = calendarService.getUpcoming();
        } else {
            int y = year != null ? year : LocalDate.now().getYear();
            int m = month != null ? month : LocalDate.now().getMonthValue();
            events = calendarService.getByMonthAndYear(y, m);
        }

        return ResponseEntity.ok(events);
    }

    /** Détail d'un événement */
    @GetMapping("/events/{id}")
    public ResponseEntity<CalendarEvent> getEventById(@PathVariable Long id) {
        return calendarService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Créer un événement */
    @PostMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<CalendarEvent> createEvent(@RequestBody CalendarEvent event) {
        log.info("Création d'un événement calendrier : {}", event.getTitre());
        return ResponseEntity.ok(calendarService.createEvent(event));
    }

    /** Modifier un événement */
    @PutMapping("/events/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<CalendarEvent> updateEvent(
            @PathVariable Long id,
            @RequestBody CalendarEvent event) {
        return ResponseEntity.ok(calendarService.updateEvent(id, event));
    }

    /** Supprimer un événement */
    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /** Déclencher une synchronisation manuelle Google Calendar */
    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<Map<String, String>> syncWithGoogle() {
        log.info("Déclenchement de la synchronisation Google Calendar");
        // TODO: synchronisation pull depuis Google (événements créés côté Google)
        return ResponseEntity.ok(Map.of("message", "Synchronisation Google Calendar déclenchée."));
    }
}
