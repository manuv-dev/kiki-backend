package com.kikitraiteur.api_kikitraiteur.Gestionnaire.mapper;

import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto.GestionnaireDemandeDto;
import org.springframework.stereotype.Component;

@Component
public class GestionnaireMapper {

    public GestionnaireDemandeDto toDto(DemandeDevis entity) {
        if (entity == null) {
            return null;
        }

        Client client = entity.getClient();
        Long clientId = client != null ? client.getId() : null;
        String clientName = client != null ? client.getName() : "Client inconnu";
        String clientEmail = client != null ? client.getEmail() : "";
        String clientPhone = client != null ? client.getPhone() : "";
        String clientType = client != null ? client.getType() : "particulier";
        String clientOrganization = client != null ? client.getOrganization() : "";

        return GestionnaireDemandeDto.builder()
                .id(entity.getId())
                .clientId(clientId)
                .clientName(clientName)
                .clientEmail(clientEmail)
                .clientPhone(clientPhone)
                .clientType(clientType)
                .clientOrganization(clientOrganization)
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
}
