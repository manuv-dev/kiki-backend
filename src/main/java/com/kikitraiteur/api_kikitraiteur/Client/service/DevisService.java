package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DevisDto;
import java.util.List;

public interface DevisService {
    DevisDto createOrUpdateDevis(DevisDto dto);
    DevisDto getDevisByDemandeId(Long demandeId);
    List<DevisDto> getAllDevis();
    byte[] downloadDevisPdf(Long devisId);
}
