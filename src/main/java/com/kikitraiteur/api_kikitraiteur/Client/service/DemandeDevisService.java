package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisResponseDto;
import java.util.List;

public interface DemandeDevisService {
    DemandeDevisResponseDto creerDemandeDevis(DemandeDevisRequestDto requestDto);
    List<DemandeDevisResponseDto> getAllDemandes();
    DemandeDevisResponseDto getDemandeById(Long id);
    List<DemandeDevisResponseDto> getDemandesByClientEmail(String email);
}
