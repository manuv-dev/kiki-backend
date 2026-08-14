package com.kikitraiteur.api_kikitraiteur.Gestionnaire.service;

import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.DashboardStatsDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.GestionnaireDemandeDto;
import java.util.List;

public interface GestionnaireService {
    List<GestionnaireDemandeDto> getAllDemandes();
    GestionnaireDemandeDto getDemandeById(Long id);
    GestionnaireDemandeDto updateDemandeStatus(Long id, String status, java.util.List<Long> propositionIds);
    DashboardStatsDto getDashboardStats(Integer year, Integer month);
}
