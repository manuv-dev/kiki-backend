package com.kikitraiteur.api_kikitraiteur.Gestionnaire.controller;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionEnvoyee;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository.PropositionEnvoyeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestionnaire/propositions-envoyees")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class PropositionEnvoyeeController {

    private final PropositionEnvoyeeRepository propositionEnvoyeeRepository;

    @GetMapping
    public ResponseEntity<List<PropositionEnvoyee>> getAllPropositionsEnvoyees() {
        log.info("Appel GET /api/gestionnaire/propositions-envoyees");
        return ResponseEntity.ok(propositionEnvoyeeRepository.findAll());
    }
}
