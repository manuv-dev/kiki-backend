package com.kikitraiteur.api_kikitraiteur.Gestionnaire.service;

import com.kikitraiteur.api_kikitraiteur.Client.exception.DemandeDevisNotFoundException;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Client.repository.ClientRepository;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DemandeDevisRepository;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.DashboardStatsDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.GestionnaireDemandeDto;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.mapper.GestionnaireMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionnaireServiceImpl implements GestionnaireService {

    private final DemandeDevisRepository demandeDevisRepository;
    private final ClientRepository clientRepository;
    private final GestionnaireMapper gestionnaireMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GestionnaireDemandeDto> getAllDemandes() {
        log.info("Récupération de la liste des demandes pour le gestionnaire");
        return demandeDevisRepository.findAllByOrderByDateSubmittedDesc().stream()
                .map(gestionnaireMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GestionnaireDemandeDto getDemandeById(Long id) {
        log.info("Récupération de la demande ID: {} pour le gestionnaire", id);
        DemandeDevis demandeDevis = demandeDevisRepository.findById(id)
                .orElseThrow(() -> new DemandeDevisNotFoundException("Aucune demande trouvée avec l'ID: " + id));
        return gestionnaireMapper.toDto(demandeDevis);
    }

    @Override
    @Transactional
    public GestionnaireDemandeDto updateDemandeStatus(Long id, String status) {
        log.info("Mise à jour du statut de la demande ID: {} en {}", id, status);
        DemandeDevis demandeDevis = demandeDevisRepository.findById(id)
                .orElseThrow(() -> new DemandeDevisNotFoundException("Aucune demande trouvée avec l'ID: " + id));
        demandeDevis.setStatus(status);
        DemandeDevis saved = demandeDevisRepository.save(demandeDevis);
        return gestionnaireMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        log.info("Calcul des statistiques réelles du tableau de bord depuis la base de données");
        List<DemandeDevis> allDemandes = demandeDevisRepository.findAllByOrderByDateSubmittedDesc();
        List<Client> allClients = clientRepository.findAll();

        long totalRequests = allDemandes.size();
        long acceptedRequests = allDemandes.stream()
                .filter(d -> isAcceptedStatus(d.getStatus()))
                .count();
        long pendingRequests = allDemandes.stream()
                .filter(d -> isPendingStatus(d.getStatus()))
                .count();
        long rejectedRequests = allDemandes.stream()
                .filter(d -> isRejectedStatus(d.getStatus()))
                .count();

        double conversionRate = totalRequests > 0
                ? (double) Math.round(((double) acceptedRequests / totalRequests) * 100.0)
                : 0.0;

        double totalRevenue = allDemandes.stream()
                .filter(d -> isAcceptedStatus(d.getStatus()))
                .mapToDouble(d -> {
                    double unitPrice = getUnitPrice(d.getPrestationId());
                    int guests = (d.getGuests() != null && d.getGuests() > 0) ? d.getGuests() : 50;
                    return unitPrice * guests;
                })
                .sum();

        long totalClients = allClients.size();
        long particuliersCount = allClients.stream()
                .filter(c -> c.getType() == null || c.getType().equalsIgnoreCase("particulier"))
                .count();
        long entreprisesCount = allClients.stream()
                .filter(c -> c.getType() != null && (c.getType().equalsIgnoreCase("entreprise") || c.getType().equalsIgnoreCase("institution")))
                .count();

        List<GestionnaireDemandeDto> recentRequests = allDemandes.stream()
                .limit(5)
                .map(gestionnaireMapper::toDto)
                .collect(Collectors.toList());

        return DashboardStatsDto.builder()
                .totalRequests(totalRequests)
                .acceptedRequests(acceptedRequests)
                .pendingRequests(pendingRequests)
                .rejectedRequests(rejectedRequests)
                .conversionRate(conversionRate)
                .totalRevenue(totalRevenue)
                .totalClients(totalClients)
                .particuliersCount(particuliersCount)
                .entreprisesCount(entreprisesCount)
                .recentRequests(recentRequests)
                .build();
    }

    private boolean isAcceptedStatus(String status) {
        return status != null && (status.equalsIgnoreCase("accepted") || status.equalsIgnoreCase("approved"));
    }

    private boolean isPendingStatus(String status) {
        return status == null || status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("en_attente") || status.equalsIgnoreCase("new");
    }

    private boolean isRejectedStatus(String status) {
        return status != null && (status.equalsIgnoreCase("rejected") || status.equalsIgnoreCase("refused"));
    }

    private double getUnitPrice(String prestationId) {
        if (prestationId == null) return 15000;
        return switch (prestationId.toLowerCase()) {
            case "traiteur" -> 15000;
            case "evenements" -> 20000;
            case "salle-diva" -> 25000;
            case "decoration" -> 5000;
            case "location" -> 3000;
            case "takeaway" -> 8000;
            case "foodtruck" -> 12000;
            default -> 15000;
        };
    }
}
