package com.kikitraiteur.api_kikitraiteur.Client.controller;

import com.kikitraiteur.api_kikitraiteur.Client.dto.ClientDto;
import com.kikitraiteur.api_kikitraiteur.Client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/clients")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findClientById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClientDto> getClientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(clientService.findClientByEmail(email));
    }
}
