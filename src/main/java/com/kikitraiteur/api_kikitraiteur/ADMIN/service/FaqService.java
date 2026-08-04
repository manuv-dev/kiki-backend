package com.kikitraiteur.api_kikitraiteur.ADMIN.service;

import com.kikitraiteur.api_kikitraiteur.ADMIN.model.Faq;
import com.kikitraiteur.api_kikitraiteur.ADMIN.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public Faq getFaqById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ non trouvée avec l'ID : " + id));
    }

    public Faq createFaq(Faq faq) {
        if (faq.getCategorie() == null || faq.getCategorie().trim().isEmpty()) {
            faq.setCategorie("Général");
        }
        return faqRepository.save(faq);
    }

    public Faq updateFaq(Long id, Faq updated) {
        Faq existing = getFaqById(id);
        if (updated.getQuestion() != null) {
            existing.setQuestion(updated.getQuestion());
        }
        if (updated.getReponse() != null) {
            existing.setReponse(updated.getReponse());
        }
        if (updated.getCategorie() != null) {
            existing.setCategorie(updated.getCategorie());
        }
        return faqRepository.save(existing);
    }

    public void deleteFaq(Long id) {
        Faq existing = getFaqById(id);
        faqRepository.delete(existing);
    }
}
