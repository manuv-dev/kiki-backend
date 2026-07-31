package com.kikitraiteur.api_kikitraiteur.Gestionnaire.controller;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.DashboardStatsDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.GestionnaireDemandeDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.UpdateStatusRequestDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.service.GestionnaireService;
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
}
