package com.kikitraiteur.api_kikitraiteur.Gestionnaire.controller;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.DashboardStatsDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.GestionnaireDemandeDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.UpdateStatusRequestDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.service.GestionnaireService;
import com.kikitraiteur.api_kikitraiteur.Client.service.DevisService;
import com.kikitraiteur.api_kikitraiteur.Client.service.ClientService;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DirectDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DevisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestionnaire")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class GestionnaireController {

    private final GestionnaireService gestionnaireService;
    private final DevisService devisService;
    private final ClientService clientService;

    @GetMapping("/demandes")
    public ResponseEntity<List<GestionnaireDemandeDto>> getAllDemandes() {
        log.info("Appel GET /api/gestionnaire/demandes");
        return ResponseEntity.ok(gestionnaireService.getAllDemandes());
    }

    @GetMapping("/demandes/{id}")
    public ResponseEntity<GestionnaireDemandeDto> getDemandeById(@PathVariable Long id) {
        log.info("Appel GET /api/gestionnaire/demandes/{}", id);
        return ResponseEntity.ok(gestionnaireService.getDemandeById(id));
    }

    @PutMapping("/demandes/{id}/status")
    public ResponseEntity<GestionnaireDemandeDto> updateDemandeStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDto requestDto) {
        log.info("Appel PUT /api/gestionnaire/demandes/{}/status avec nouveau statut : {}", id, requestDto.getStatus());
        return ResponseEntity.ok(gestionnaireService.updateDemandeStatus(id, requestDto.getStatus()));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        log.info("Appel GET /api/gestionnaire/dashboard/stats");
        return ResponseEntity.ok(gestionnaireService.getDashboardStats());
    }

    @PostMapping("/devis/direct")
    public ResponseEntity<DevisDto> createDirectDevis(@RequestBody DirectDevisRequestDto requestDto) {
        log.info("Appel POST /api/gestionnaire/devis/direct");
        return ResponseEntity.ok(devisService.createDirectDevis(requestDto));
    }

    @PostMapping("/clients")
    public ResponseEntity<?> createClient(@RequestBody java.util.Map<String, String> payload) {
        log.info("Appel POST /api/gestionnaire/clients");
        String email = payload.get("email");
        String name = payload.get("name");
        String phone = payload.get("phone");
        String type = payload.get("type");
        String organization = payload.get("organization");
        
        com.kikitraiteur.api_kikitraiteur.Client.model.Client newClient = clientService.getOrCreateClient(email, name, phone, type, organization);
        return ResponseEntity.ok(newClient);
    }
}
