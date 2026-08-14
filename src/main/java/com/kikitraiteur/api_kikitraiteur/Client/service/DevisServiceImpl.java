package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DevisDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.Devis;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DevisRepository;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DemandeDevisRepository;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DirectDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ClientService clientService;
    private final ClientRepository clientRepository;

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
            if (dto.getGuests() != null) devis.setGuests(dto.getGuests());
            if (dto.getLocation() != null) devis.setLocation(dto.getLocation());
            if (dto.getDate() != null) devis.setDate(dto.getDate());
            if (dto.getTime() != null) devis.setTime(dto.getTime());
            if (dto.getStatus() != null) devis.setStatus(dto.getStatus());
            if (dto.getClientEmail() != null) devis.setClientEmail(dto.getClientEmail());
            if (dto.getClientName() != null) devis.setClientName(dto.getClientName());
            if (dto.getClientPhone() != null) devis.setClientPhone(dto.getClientPhone());
            if (dto.getGestionnaireName() != null) devis.setGestionnaireName(dto.getGestionnaireName());
        } else {
            devis = Devis.builder()
                    .devisRef(dto.getDevisRef() != null ? dto.getDevisRef() : "#DEV-" + System.currentTimeMillis())
                    .demandeId(dto.getDemandeId())
                    .clientName(dto.getClientName())
                    .clientEmail(dto.getClientEmail())
                    .clientPhone(dto.getClientPhone())
                    .gestionnaireName(dto.getGestionnaireName())
                    .prestationId(dto.getPrestationId())
                    .signatureGastronomique(dto.getSignatureGastronomique() != null ? dto.getSignatureGastronomique() : "Menu Signature Kiki Traiteur")
                    .guests(dto.getGuests())
                    .location(dto.getLocation())
                    .date(dto.getDate())
                    .time(dto.getTime())
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
    @Transactional
    public DevisDto createDirectDevis(DirectDevisRequestDto dto) {
        Client client;
        if (dto.getClientId() != null) {
            client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + dto.getClientId()));
        } else {
            client = clientService.getOrCreateClient(
                    dto.getNewClientEmail(),
                    dto.getNewClientName(),
                    dto.getNewClientPhone(),
                    dto.getNewClientType(),
                    dto.getNewClientOrganization()
            );
        }

        // Créer une DemandeDevis virtuelle pour l'historique/listes
        com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis demande = com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis.builder()
                .client(client)
                .clientType("particulier")
                .prestationId(dto.getPrestationId() != null ? dto.getPrestationId() : "salle-diva")
                .date(dto.getDate() != null ? dto.getDate() : LocalDate.now().toString())
                .time(dto.getTime() != null ? dto.getTime() : "19:00")
                .guests(dto.getGuests() != null ? dto.getGuests() : 50)
                .locationType("autre")
                .locationDetails(dto.getLocation() != null ? dto.getLocation() : "Dakar")
                .status("sent")
                .build();
        
        demande = demandeDevisRepository.save(demande);

        // Créer le Devis
        DevisDto devisDto = DevisDto.builder()
                .demandeId(demande.getId())
                .clientName(client.getName())
                .clientEmail(client.getEmail())
                .clientPhone(client.getPhone())
                .gestionnaireName(dto.getGestionnaireName())
                .prestationId(dto.getPrestationId())
                .signatureGastronomique(dto.getSignatureGastronomique())
                .guests(dto.getGuests())
                .location(dto.getLocation())
                .date(dto.getDate())
                .time(dto.getTime())
                .tvaRate(dto.getTvaRate())
                .discount(dto.getDiscount())
                .items(dto.getItems())
                .status("sent")
                .build();

        return createOrUpdateDevis(devisDto);
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

    @Override
    @Transactional
    public void sendDevisByMail(Long devisId) {
        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé avec l'ID: " + devisId));

        // Mettre à jour le statut en "sent" si ce n'est pas déjà le cas
        if (!"sent".equalsIgnoreCase(devis.getStatus())) {
            devis.setStatus("sent");
            devisRepository.save(devis);
        }

        // Mettre à jour le statut de la demande liée
        if (devis.getDemandeId() != null) {
            demandeDevisRepository.findById(devis.getDemandeId()).ifPresent(d -> {
                d.setStatus("sent");
                demandeDevisRepository.save(d);
            });
        }

        // Envoyer l'email avec le PDF en pièce jointe
        devisMailService.sendDevisEmailWithPdf(devis);
    }

    private DevisDto mapToDto(Devis d) {
        return DevisDto.builder()
                .id(d.getId())
                .devisRef(d.getDevisRef())
                .demandeId(d.getDemandeId())
                .clientName(d.getClientName())
                .clientEmail(d.getClientEmail())
                .clientPhone(d.getClientPhone())
                .gestionnaireName(d.getGestionnaireName())
                .prestationId(d.getPrestationId())
                .signatureGastronomique(d.getSignatureGastronomique())
                .guests(d.getGuests())
                .location(d.getLocation())
                .date(d.getDate())
                .time(d.getTime())
                .dateCreated(d.getDateCreated())
                .tvaRate(d.getTvaRate())
                .discount(d.getDiscount())
                .status(d.getStatus())
                .items(d.getItems())
                .build();
    }
}
