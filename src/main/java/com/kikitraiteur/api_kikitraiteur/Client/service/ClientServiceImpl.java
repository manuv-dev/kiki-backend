package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.dto.ClientDto;
import com.kikitraiteur.api_kikitraiteur.Client.exception.ClientNotFoundException;
import com.kikitraiteur.api_kikitraiteur.Client.mapper.ClientMapper;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    @Transactional
    public Client getOrCreateClient(String email, String name, String phone, String type, String organization) {
        String finalPhone = (phone != null && !phone.isBlank()) ? phone : "Non renseigné";
        log.info("Vérification de l'existence du client avec l'email: {} et le téléphone: {}", email, finalPhone);
        List<Client> existingClients = clientRepository.findAllByEmailAndPhoneOrTelephone(email, finalPhone);

        if (!existingClients.isEmpty()) {
            Client existing = existingClients.get(0);
            log.info("Client existant trouvé pour l'email: {} et le téléphone: {} (ID: {})", email, finalPhone, existing.getId());
            if (name != null && !name.isBlank()) {
                existing.setName(name);
                existing.setNameCol(name);
            }
            if (phone != null && !phone.isBlank()) {
                existing.setPhone(phone);
                existing.setTelephone(phone);
            }
            if (type != null && !type.isBlank()) existing.setType(type);
            if (organization != null) existing.setOrganization(organization);
            return clientRepository.save(existing);
        }

        log.info("Client inexistant pour l'email: {} et le téléphone: {}. Création d'un nouveau client...", email, finalPhone);
        String finalName = (name != null && !name.isBlank()) ? name : "Client Kiki Traiteur";
        Client newClient = Client.builder()
                .email(email)
                .name(finalName)
                .nameCol(finalName)
                .phone(finalPhone)
                .telephone(finalPhone)
                .type(type != null && !type.isBlank() ? type : "particulier")
                .organization(organization)
                .build();

        return clientRepository.save(newClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto findClientByEmail(String email) {
        Client client = clientRepository.findFirstByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException("Aucun client trouvé avec l'email: " + email));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto findClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Aucun client trouvé avec l'ID: " + id));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClientDto updateClient(Long id, String email, String name, String phone, String type, String organization) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Aucun client trouvé avec l'ID: " + id));

        if (email != null && !email.isBlank()) client.setEmail(email);
        if (name != null && !name.isBlank()) {
            client.setName(name);
            client.setNameCol(name);
        }
        if (phone != null && !phone.isBlank()) {
            client.setPhone(phone);
            client.setTelephone(phone);
        }
        if (type != null && !type.isBlank()) client.setType(type);
        client.setOrganization(organization);

        return clientMapper.toDto(clientRepository.save(client));
    }

    @Override
    @Transactional
    public void linkUserToClient(Long clientId, Long appUserId) {
        clientRepository.findById(clientId).ifPresent(client -> {
            client.setClientUserId(appUserId);
            clientRepository.save(client);
            log.info("Client #{} lié à l'AppUser #{}", clientId, appUserId);
        });
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Aucun client trouvé avec l'ID: " + id));
        clientRepository.delete(client);
        log.info("Client #{} supprimé avec succès.", id);
    }
}

