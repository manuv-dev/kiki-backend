package com.kikitraiteur.api_kikitraiteur.ADMIN.controller;

import com.kikitraiteur.api_kikitraiteur.ADMIN.model.Temoignage;
import com.kikitraiteur.api_kikitraiteur.ADMIN.service.TemoignageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/temoignages", "/api/testimonials"})
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class TemoignageController {

    private final TemoignageService temoignageService;

    @GetMapping
    public ResponseEntity<List<Temoignage>> getAllTemoignages() {
        return ResponseEntity.ok(temoignageService.getAllTemoignages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Temoignage> getTemoignageById(@PathVariable Long id) {
        return ResponseEntity.ok(temoignageService.getTemoignageById(id));
    }

    @PostMapping
    public ResponseEntity<Temoignage> createTemoignage(@RequestBody Temoignage temoignage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(temoignageService.createTemoignage(temoignage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Temoignage> updateTemoignage(@PathVariable Long id, @RequestBody Temoignage updated) {
        return ResponseEntity.ok(temoignageService.updateTemoignage(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemoignage(@PathVariable Long id) {
        temoignageService.deleteTemoignage(id);
        return ResponseEntity.noContent().build();
    }
}
