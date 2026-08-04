package com.kikitraiteur.api_kikitraiteur.ADMIN.service;

import com.kikitraiteur.api_kikitraiteur.ADMIN.model.Temoignage;
import com.kikitraiteur.api_kikitraiteur.ADMIN.repository.TemoignageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemoignageService {

    private final TemoignageRepository temoignageRepository;

    public List<Temoignage> getAllTemoignages() {
        return temoignageRepository.findAll();
    }

    public Temoignage getTemoignageById(Long id) {
        return temoignageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Témoignage non trouvé avec l'ID : " + id));
    }

    public Temoignage createTemoignage(Temoignage temoignage) {
        if (temoignage.getNote() == null || temoignage.getNote() < 1 || temoignage.getNote() > 5) {
            temoignage.setNote(5);
        }
        return temoignageRepository.save(temoignage);
    }

    public Temoignage updateTemoignage(Long id, Temoignage updated) {
        Temoignage existing = getTemoignageById(id);
        if (updated.getTemoignage() != null) {
            existing.setTemoignage(updated.getTemoignage());
        }
        if (updated.getNomClient() != null) {
            existing.setNomClient(updated.getNomClient());
        }
        if (updated.getTitreFonction() != null) {
            existing.setTitreFonction(updated.getTitreFonction());
        }
        if (updated.getNote() != null) {
            existing.setNote(updated.getNote());
        }
        return temoignageRepository.save(existing);
    }

    public void deleteTemoignage(Long id) {
        Temoignage existing = getTemoignageById(id);
        temoignageRepository.delete(existing);
    }
}
