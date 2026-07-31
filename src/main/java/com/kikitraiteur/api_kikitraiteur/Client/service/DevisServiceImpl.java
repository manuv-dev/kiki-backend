package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DevisDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.Devis;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DevisRepository;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DemandeDevisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevisServiceImpl implements DevisService {

    private final DevisRepository devisRepository;
    private final DemandeDevisRepository demandeDevisRepository;
    private final DevisMailService devisMailService;
    private final DevisPdfService devisPdfService;

    @Override
    @Transactional
    public DevisDto createOrUpdateDevis(DevisDto dto) {
        Optional<Devis> existingOpt = devisRepository.findByDemandeId(dto.getDemandeId());
        Devis devis;
        if (existingOpt.isPresent()) {
            devis = existingOpt.get();
            if (dto.getItems() != null) devis.setItems(dto.getItems());
            if (dto.getTvaRate() != null) devis.setTvaRate(dto.getTvaRate());
            if (dto.getDiscount() != null) devis.setDiscount(dto.getDiscount());
            if (dto.getSignatureGastronomique() != null) devis.setSignatureGastronomique(dto.getSignatureGastronomique());
            if (dto.getStatus() != null) devis.setStatus(dto.getStatus());
            if (dto.getClientEmail() != null) devis.setClientEmail(dto.getClientEmail());
            if (dto.getClientName() != null) devis.setClientName(dto.getClientName());
        } else {
            devis = Devis.builder()
                    .devisRef(dto.getDevisRef() != null ? dto.getDevisRef() : "#DEV-" + System.currentTimeMillis())
                    .demandeId(dto.getDemandeId())
                    .clientName(dto.getClientName())
                    .clientEmail(dto.getClientEmail())
                    .prestationId(dto.getPrestationId())
                    .signatureGastronomique(dto.getSignatureGastronomique() != null ? dto.getSignatureGastronomique() : "Menu Signature Kiki Traiteur")
                    .dateCreated(LocalDate.now().toString())
                    .tvaRate(dto.getTvaRate() != null ? dto.getTvaRate() : 0.0)
                    .discount(dto.getDiscount() != null ? dto.getDiscount() : 0.0)
                    .status(dto.getStatus() != null ? dto.getStatus() : "sent")
                    .items(dto.getItems())
                    .build();
        }

        devis = devisRepository.save(devis);

        // Si le devis est envoyé par email ('sent'), on met à jour le statut de la demande et on envoie le mail avec PDF
        if (devis.getStatus() != null && ("sent".equalsIgnoreCase(devis.getStatus()) || "envoyé".equalsIgnoreCase(devis.getStatus()))) {
            Optional<DemandeDevis> demandeOpt = demandeDevisRepository.findById(devis.getDemandeId());
            if (demandeOpt.isPresent()) {
                DemandeDevis d = demandeOpt.get();
                d.setStatus("sent");
                demandeDevisRepository.save(d);
            }
            devisMailService.sendDevisEmailWithPdf(devis);
        }

        return mapToDto(devis);
    }

    @Override
    @Transactional(readOnly = true)
    public DevisDto getDevisByDemandeId(Long demandeId) {
        return devisRepository.findByDemandeId(demandeId)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevisDto> getAllDevis() {
        return devisRepository.findAllByOrderByIdDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadDevisPdf(Long devisId) {
        try {
            Devis devis = devisRepository.findById(devisId)
                    .orElseThrow(() -> new RuntimeException("Devis non trouvé"));
            return devisPdfService.generateDevisPdf(devis);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de génération PDF: " + e.getMessage(), e);
        }
    }

    private DevisDto mapToDto(Devis d) {
        return DevisDto.builder()
                .id(d.getId())
                .devisRef(d.getDevisRef())
                .demandeId(d.getDemandeId())
                .clientName(d.getClientName())
                .clientEmail(d.getClientEmail())
                .prestationId(d.getPrestationId())
                .signatureGastronomique(d.getSignatureGastronomique())
                .dateCreated(d.getDateCreated())
                .tvaRate(d.getTvaRate())
                .discount(d.getDiscount())
                .status(d.getStatus())
                .items(d.getItems())
                .build();
    }
}
