package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisResponseDto;
import com.kikitraiteur.api_kikitraiteur.Client.exception.DemandeDevisNotFoundException;
import com.kikitraiteur.api_kikitraiteur.Client.exception.InvalidEmailException;
import com.kikitraiteur.api_kikitraiteur.Client.exception.InvalidEventDateException;
import com.kikitraiteur.api_kikitraiteur.Client.exception.InvalidSenegalPhoneException;
import com.kikitraiteur.api_kikitraiteur.Client.mapper.DemandeDevisMapper;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DemandeDevisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandeDevisServiceImpl implements DemandeDevisService {

    private final DemandeDevisRepository demandeDevisRepository;
    private final ClientService clientService;
    private final DemandeDevisMapper demandeDevisMapper;

    @Override
    @Transactional
    public DemandeDevisResponseDto creerDemandeDevis(DemandeDevisRequestDto requestDto) {
        log.info("Traitement et validation d'une nouvelle demande de devis pour l'email client: {}", requestDto.getEmail());
        validateDemandeDevis(requestDto);

        // 1. Vérifier si le client existe, sinon l'enregistrer
        Client client = clientService.getOrCreateClient(
                requestDto.getEmail(),
                requestDto.getName(),
                requestDto.getPhone(),
                requestDto.getClientType(),
                requestDto.getOrganization()
        );

        // 2. Créer l'entité DemandeDevis
        DemandeDevis demandeDevis = demandeDevisMapper.toEntity(requestDto, client);

        // 3. Sauvegarder dans la base de données
        DemandeDevis savedDemande = demandeDevisRepository.save(demandeDevis);
        log.info("Demande de devis enregistrée avec succès sous l'ID: {}", savedDemande.getId());

        return demandeDevisMapper.toResponseDto(savedDemande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeDevisResponseDto> getAllDemandes() {
        return demandeDevisRepository.findAll().stream()
                .map(demandeDevisMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DemandeDevisResponseDto getDemandeById(Long id) {
        DemandeDevis demandeDevis = demandeDevisRepository.findById(id)
                .orElseThrow(() -> new DemandeDevisNotFoundException("Demande de devis non trouvée avec l'ID: " + id));
        return demandeDevisMapper.toResponseDto(demandeDevis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeDevisResponseDto> getDemandesByClientEmail(String email) {
        return demandeDevisRepository.findByClientEmail(email).stream()
                .map(demandeDevisMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void validateDemandeDevis(DemandeDevisRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Les données de la demande de devis ne peuvent pas être nulles.");
        }

        // 1. Validation de l'email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (dto.getEmail() == null || dto.getEmail().isBlank() || !dto.getEmail().matches(emailRegex)) {
            throw new InvalidEmailException("L'adresse email '" + dto.getEmail() + "' n'est pas valide.");
        }

        // 2. Validation du numéro de téléphone (Standard Sénégalais)
        String senegalPhoneRegex = "^(\\+221|00221)?\\s*(7[05678]|33)\\s*(\\d\\s*){7}$";
        if (dto.getPhone() == null || dto.getPhone().isBlank() || !dto.getPhone().matches(senegalPhoneRegex)) {
            throw new InvalidSenegalPhoneException("Le numéro de téléphone '" + dto.getPhone() + "' n'est pas conforme au standard sénégalais (ex: +221 77 777 77 77 ou 70... / 75... / 76... / 77... / 78... / 33...).");
        }

        // 3. Validation de la date d'événement (Pas de date dans le passé)
        if (dto.getDate() == null || dto.getDate().isBlank() || "Non précisée".equalsIgnoreCase(dto.getDate())) {
            throw new InvalidEventDateException("La date de l'événement est obligatoire.");
        }
        try {
            LocalDate eventDate = LocalDate.parse(dto.getDate());
            if (eventDate.isBefore(LocalDate.now())) {
                throw new InvalidEventDateException("La date de l'événement (" + dto.getDate() + ") ne peut pas être antérieure à la date du jour (" + LocalDate.now() + ").");
            }
        } catch (DateTimeParseException e) {
            throw new InvalidEventDateException("Le format de la date de l'événement '" + dto.getDate() + "' est invalide (attendu: YYYY-MM-DD).");
        }
    }
}
