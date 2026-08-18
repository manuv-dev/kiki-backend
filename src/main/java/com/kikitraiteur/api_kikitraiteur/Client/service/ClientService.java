package com.kikitraiteur.api_kikitraiteur.Client.service;

import java.util.List;

import com.kikitraiteur.api_kikitraiteur.Client.dto.ClientDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;

public interface ClientService {
    Client getOrCreateClient(String email, String name, String phone, String type, String organization);
    ClientDto findClientByEmail(String email);
    ClientDto findClientById(Long id);
    List<ClientDto> getAllClients();
    ClientDto updateClient(Long id, String email, String name, String phone, String type, String organization);
    /** Lie l'AppUser (compte MyKiki) à son client */
    void linkUserToClient(Long clientId, Long appUserId);
}

