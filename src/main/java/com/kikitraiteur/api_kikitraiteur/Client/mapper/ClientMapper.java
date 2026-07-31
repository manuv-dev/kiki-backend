package com.kikitraiteur.api_kikitraiteur.Client.mapper;

import com.kikitraiteur.api_kikitraiteur.Client.dto.ClientDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .type(client.getType())
                .organization(client.getOrganization())
                .createdAt(client.getCreatedAt())
                .build();
    }

    public Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }
        return Client.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .type(dto.getType())
                .organization(dto.getOrganization())
                .build();
    }
}
