package com.kikitraiteur.api_kikitraiteur.ADMIN.controller;

import com.kikitraiteur.api_kikitraiteur.ADMIN.model.Faq;
import com.kikitraiteur.api_kikitraiteur.ADMIN.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getAllFaqs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faq> getFaqById(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping
    public ResponseEntity<Faq> createFaq(@RequestBody Faq faq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(faqService.createFaq(faq));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faq> updateFaq(@PathVariable Long id, @RequestBody Faq updated) {
        return ResponseEntity.ok(faqService.updateFaq(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
