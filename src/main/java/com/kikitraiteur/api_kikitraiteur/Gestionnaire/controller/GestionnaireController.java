package com.kikitraiteur.api_kikitraiteur.Gestionnaire.controller;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DirectDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DevisDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DemandeDevisRepository;
import com.kikitraiteur.api_kikitraiteur.Client.service.ClientService;
import com.kikitraiteur.api_kikitraiteur.Client.service.DevisService;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.*;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionEnvoyee;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository.PropositionEnvoyeeRepository;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.service.GestionnaireService;
import com.kikitraiteur.api_kikitraiteur.calendar.model.CalendarEvent;
import com.kikitraiteur.api_kikitraiteur.calendar.service.CalendarService;
import com.kikitraiteur.api_kikitraiteur.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestionnaire")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'PERSONNEL')")
public class GestionnaireController {

    private final GestionnaireService gestionnaireService;
    private final DevisService devisService;
    private final ClientService clientService;
    private final DemandeDevisRepository demandeDevisRepository;
    private final PropositionEnvoyeeRepository propositionEnvoyeeRepository;
    private final NotificationService notificationService;
    private final CalendarService calendarService;

    // =============================================
    // DEMANDES
    // =============================================

    @GetMapping("/demandes")
    public ResponseEntity<List<GestionnaireDemandeDto>> getAllDemandes(
            @RequestParam(required = false) String status) {
        log.info("GET /api/gestionnaire/demandes status={}", status);
        if (status != null) {
            return ResponseEntity.ok(demandeDevisRepository.findByStatusOrderByDateSubmittedDesc(status)
                    .stream().map(d -> gestionnaireService.getDemandeById(d.getId())).toList());
        }
        return ResponseEntity.ok(gestionnaireService.getAllDemandes());
    }

    @GetMapping("/demandes/{id}")
    public ResponseEntity<GestionnaireDemandeDto> getDemandeById(@PathVariable Long id) {
        return ResponseEntity.ok(gestionnaireService.getDemandeById(id));
    }

    /** Mise à jour générique du statut (pour compatibilité) */
    @PutMapping("/demandes/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<GestionnaireDemandeDto> updateDemandeStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDto requestDto) {
        log.info("PUT /api/gestionnaire/demandes/{}/status → {}", id, requestDto.getStatus());
        return ResponseEntity.ok(gestionnaireService.updateDemandeStatus(
                id, requestDto.getStatus(), requestDto.getPropositionIds()));
    }

    /**
     * Accepter une demande : envoie des propositions au client.
     * → Passe la demande en "propositions_envoyees"
     * → Crée les PropositionEnvoyee
     * → Notifie le client (si clientUserId disponible)
     */
    @PostMapping("/demandes/{id}/accepter")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<GestionnaireDemandeDto> accepterDemande(
            @PathVariable Long id,
            @RequestBody AccepterDemandeRequest request) {
        log.info("POST /api/gestionnaire/demandes/{}/accepter", id);

        GestionnaireDemandeDto result = gestionnaireService.updateDemandeStatus(
                id, "propositions_envoyees", request.getPropositionIds());

        // Récupérer infos client pour notification
        demandeDevisRepository.findById(id).ifPresent(demande -> {
            String clientName = demande.getClient() != null ? demande.getClient().getName() : "Client";
            // Notifier le client (clientUserId peut être null si pas de compte MyKiki)
            Long clientUserId = demande.getClient() != null ? demande.getClient().getClientUserId() : null;
            if (clientUserId != null) {
                request.getPropositionIds().forEach(propId ->
                    notificationService.propositionEnvoyee(clientUserId, id, propId)
                );
            }
        });

        return ResponseEntity.ok(result);
    }

    /**
     * Refuser une demande.
     * → Passe la demande en "rejected"
     * → Notifie le client si compte MyKiki
     */
    @PostMapping("/demandes/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<GestionnaireDemandeDto> refuserDemande(
            @PathVariable Long id,
            @RequestBody(required = false) RefuserDemandeRequest request) {
        log.info("POST /api/gestionnaire/demandes/{}/refuser", id);

        demandeDevisRepository.findById(id).ifPresent(d -> {
            d.setStatus("rejected");
            if (request != null && request.getMotif() != null) {
                d.setMotifRefus(request.getMotif());
            }
            demandeDevisRepository.save(d);
        });

        GestionnaireDemandeDto dto = gestionnaireService.getDemandeById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Valider la sélection du client → rend la demande "aboutie".
     * → Crée un événement dans le calendrier
     * → Synchronise avec Google Calendar
     * → Notifie client et équipe
     */
    @PostMapping("/demandes/{id}/valider-selection")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<?> validerSelection(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> extraData) {

        log.info("POST /api/gestionnaire/demandes/{}/valider-selection", id);

        return demandeDevisRepository.findById(id).map(demande -> {
            // Marquer la demande comme aboutie
            demande.setStatus("aboutis");
            demandeDevisRepository.save(demande);

            // Marquer la proposition sélectionnée comme validée par le gestionnaire
            List<PropositionEnvoyee> selected = propositionEnvoyeeRepository
                    .findByDemandeId(id).stream()
                    .filter(p -> "selectionnee_client".equals(p.getStatus()))
                    .toList();
            selected.forEach(p -> {
                p.setStatus("validee_gestionnaire");
                p.setGestionnaireValidatedAt(java.time.LocalDateTime.now());
                propositionEnvoyeeRepository.save(p);
            });

            // Créer l'événement calendrier
            String clientName = demande.getClient() != null ? demande.getClient().getName() : "Client";
            Long clientId = demande.getClient() != null ? demande.getClient().getId() : null;
            String lieu = demande.getLocationDetails() != null ? demande.getLocationDetails() : demande.getLocationType();

            LocalDate dateEvenement = null;
            try {
                if (demande.getDate() != null && !demande.getDate().isBlank()) {
                    dateEvenement = LocalDate.parse(demande.getDate());
                }
            } catch (DateTimeParseException e) {
                log.warn("Impossible de parser la date : {}", demande.getDate());
            }
            if (dateEvenement == null) dateEvenement = LocalDate.now().plusMonths(1);

            CalendarEvent calEvent = calendarService.createFromDemande(
                    id,
                    "Événement " + clientName + " — " + (demande.getPrestationId() != null ? demande.getPrestationId() : "Traiteur"),
                    clientName,
                    clientId,
                    lieu,
                    dateEvenement,
                    demande.getGuests(),
                    demande.getPrestationId() != null ? demande.getPrestationId() : "traiteur"
            );

            demande.setCalendarEventId(calEvent.getId());
            demandeDevisRepository.save(demande);

            // Notifications
            Long clientUserId = demande.getClient() != null ? demande.getClient().getClientUserId() : null;
            notificationService.demandeAboutie(id, clientUserId, clientName);

            log.info("Demande {} aboutie — Événement calendrier #{} créé", id, calEvent.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "Demande validée ! Événement créé dans le calendrier.",
                    "calendarEventId", calEvent.getId(),
                    "googleEventId", calEvent.getGoogleEventId() != null ? calEvent.getGoogleEventId() : "non synchronisé"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // =============================================
    // STATISTIQUES
    // =============================================

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(gestionnaireService.getDashboardStats(year, month));
    }

    // =============================================
    // DEVIS & CLIENTS
    // =============================================

    @PostMapping("/devis/direct")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<DevisDto> createDirectDevis(@RequestBody DirectDevisRequestDto requestDto) {
        return ResponseEntity.ok(devisService.createDirectDevis(requestDto));
    }

    @PostMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    public ResponseEntity<?> createClient(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String name = payload.get("name");
        String phone = payload.get("phone");
        String type = payload.get("type");
        String organization = payload.get("organization");
        return ResponseEntity.ok(clientService.getOrCreateClient(email, name, phone, type, organization));
    }
}

