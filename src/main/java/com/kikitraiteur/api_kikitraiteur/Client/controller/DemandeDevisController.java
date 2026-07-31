package com.kikitraiteur.api_kikitraiteur.Client.controller;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisResponseDto;
import com.kikitraiteur.api_kikitraiteur.Client.service.DemandeDevisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/devis")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class DemandeDevisController {

    private final DemandeDevisService demandeDevisService;

    @PostMapping
    public ResponseEntity<DemandeDevisResponseDto> creerDemandeDevis(@Valid @RequestBody DemandeDevisRequestDto requestDto) {
        log.info("Réception d'une requête de demande de devis de {}", requestDto.getClientEmail());
        DemandeDevisResponseDto response = demandeDevisService.creerDemandeDevis(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DemandeDevisResponseDto>> getAllDemandes() {
        return ResponseEntity.ok(demandeDevisService.getAllDemandes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeDevisResponseDto> getDemandeById(@PathVariable Long id) {
        return ResponseEntity.ok(demandeDevisService.getDemandeById(id));
    }

    @GetMapping("/client/{email}")
    public ResponseEntity<List<DemandeDevisResponseDto>> getDemandesByClientEmail(@PathVariable String email) {
        return ResponseEntity.ok(demandeDevisService.getDemandesByClientEmail(email));
    }
}
