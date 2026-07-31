package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.ClientDto;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import java.util.List;

public interface ClientService {
    Client getOrCreateClient(String email, String name, String phone, String type, String organization);
    ClientDto findClientByEmail(String email);
    ClientDto findClientById(Long id);
    List<ClientDto> getAllClients();
}
