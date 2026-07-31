package com.kikitraiteur.api_kikitraiteur.Client.controller;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DevisDto;
import com.kikitraiteur.api_kikitraiteur.Client.service.DevisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/devis", "/api/gestionnaire/devis"})
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class DevisController {

    private final DevisService devisService;

    @PostMapping
    public ResponseEntity<DevisDto> createOrUpdateDevis(@RequestBody DevisDto dto) {
        log.info("Appel POST /api/devis - demandeId={}, ref={}", dto.getDemandeId(), dto.getDevisRef());
        return ResponseEntity.ok(devisService.createOrUpdateDevis(dto));
    }

    @GetMapping
    public ResponseEntity<List<DevisDto>> getAllDevis() {
        log.info("Appel GET /api/devis");
        return ResponseEntity.ok(devisService.getAllDevis());
    }

    @GetMapping("/demande/{demandeId}")
    public ResponseEntity<DevisDto> getDevisByDemandeId(@PathVariable Long demandeId) {
        log.info("Appel GET /api/devis/demande/{}", demandeId);
        DevisDto d = devisService.getDevisByDemandeId(demandeId);
        return d != null ? ResponseEntity.ok(d) : ResponseEntity.noContent().build();
    }

    @GetMapping("/{devisId}/pdf")
    public ResponseEntity<byte[]> downloadDevisPdf(@PathVariable Long devisId) {
        log.info("Appel GET /api/devis/{}/pdf", devisId);
        byte[] pdfBytes = devisService.downloadDevisPdf(devisId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Devis_KikiTraiteur_" + devisId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
