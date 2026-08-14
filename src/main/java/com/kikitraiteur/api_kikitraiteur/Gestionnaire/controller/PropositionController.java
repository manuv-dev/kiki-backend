package com.kikitraiteur.api_kikitraiteur.Gestionnaire.controller;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.Proposition;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.service.PropositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestionnaire/propositions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permettre l'accès depuis le front Angular
public class PropositionController {

    private final PropositionService propositionService;

    @GetMapping
    public ResponseEntity<List<Proposition>> getAllPropositions() {
        return ResponseEntity.ok(propositionService.getAllPropositions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proposition> getPropositionById(@PathVariable Long id) {
        return ResponseEntity.ok(propositionService.getPropositionById(id));
    }

    @PostMapping
    public ResponseEntity<Proposition> createProposition(@RequestBody Proposition proposition) {
        return ResponseEntity.ok(propositionService.createProposition(proposition));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proposition> updateProposition(@PathVariable Long id, @RequestBody Proposition proposition) {
        return ResponseEntity.ok(propositionService.updateProposition(id, proposition));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposition(@PathVariable Long id) {
        propositionService.deleteProposition(id);
        return ResponseEntity.noContent().build();
    }
}
