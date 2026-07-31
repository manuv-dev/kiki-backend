package com.kikitraiteur.api_kikitraiteur.Client.mapper;

import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisRequestDto;
import com.kikitraiteur.api_kikitraiteur.Client.dto.DemandeDevisResponseDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemandeDevisMapper {

    private final ClientMapper clientMapper;

    public DemandeDevisResponseDto toResponseDto(DemandeDevis entity) {
        if (entity == null) {
            return null;
        }
        return DemandeDevisResponseDto.builder()
                .id(entity.getId())
                .client(clientMapper.toDto(entity.getClient()))
                .prestationId(entity.getPrestationId())
                .prestationTitle(entity.getPrestationTitle())
                .date(entity.getDate())
                .time(entity.getTime())
                .guests(entity.getGuests())
                .isInstitution(entity.getIsInstitution())
                .organization(entity.getOrganization())
                .location(entity.getLocation())
                .cuisine(entity.getCuisine())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .dateSubmitted(entity.getDateSubmitted())
                .build();
    }

    public DemandeDevis toEntity(DemandeDevisRequestDto dto, Client client) {
        if (dto == null) {
            return null;
        }
        return DemandeDevis.builder()
                .client(client)
                .prestationId(dto.getPrestationId() != null ? dto.getPrestationId() : "restauration-entreprise")
                .prestationTitle(dto.getPrestationTitle() != null ? dto.getPrestationTitle() : "Prestation sur mesure")
                .date(dto.getDate() != null ? dto.getDate() : "Non précisée")
                .time(dto.getTime() != null ? dto.getTime() : "19:00")
                .guests(dto.getGuests() != null ? dto.getGuests() : 50)
                .isInstitution(dto.getIsInstitution() != null ? dto.getIsInstitution() : "entreprise".equalsIgnoreCase(dto.getClientType()))
                .organization(dto.getOrganization())
                .location(dto.getLocation())
                .cuisine(dto.getCuisine())
                .message(dto.getMessage())
                .status("pending")
                .build();
    }
}
