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
                .evenementNature(entity.getEvenementNature())
                .date(entity.getDate())
                .time(entity.getTime())
                .guests(entity.getGuests())
                .organization(entity.getOrganization())
                .locationType(entity.getLocationType())
                .locationDetails(entity.getLocationDetails())
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
                .clientType(dto.getClientType() != null ? dto.getClientType() : "particulier")
                .prestationId(dto.getPrestationId() != null ? dto.getPrestationId() : "restauration-entreprise")
                .evenementNature(dto.getEvenementNature())
                .date(dto.getDate() != null ? dto.getDate() : "Non précisée")
                .time(dto.getTime() != null ? dto.getTime() : "19:00")
                .guests(dto.getGuests() != null ? dto.getGuests() : 50)
                .organization(dto.getOrganization())
                .locationType(dto.getLocationType())
                .locationDetails(dto.getLocationDetails())
                .message(dto.getMessage())
                .status("pending")
                .build();
    }
}
