package com.kikitraiteur.api_kikitraiteur.Client.controller;

import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.model.DemandeDevis;
import com.kikitraiteur.api_kikitraiteur.Client.repository.ClientRepository;
import com.kikitraiteur.api_kikitraiteur.Client.repository.DemandeDevisRepository;
import com.kikitraiteur.api_kikitraiteur.Client.service.ClientService;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.model.PropositionEnvoyee;
import com.kikitraiteur.api_kikitraiteur.Gestionnaire.repository.PropositionEnvoyeeRepository;
import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import com.kikitraiteur.api_kikitraiteur.auth.repository.AppUserRepository;
import com.kikitraiteur.api_kikitraiteur.notification.service.NotificationService;
import com.kikitraiteur.api_kikitraiteur.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mykiki")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class MyKikiController {

    private final AppUserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ClientService clientService;
    private final DemandeDevisRepository demandeDevisRepository;
    private final PropositionEnvoyeeRepository propositionEnvoyeeRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // =============================================
    // INSCRIPTION CLIENT (PUBLIC)
    // =============================================

    @GetMapping("/captcha")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        int a = (int) (Math.random() * 10) + 1;
        int b = (int) (Math.random() * 10) + 1;
        int answer = a + b;
        String question = a + " + " + b;
        
        // On signe la réponse pour que le frontend ne puisse pas tricher (Hash MD5 simple avec un sel)
        String salt = "KikiSecretSalt2024";
        String token = org.springframework.util.DigestUtils.md5DigestAsHex((answer + salt).getBytes());
        
        return ResponseEntity.ok(Map.of(
                "question", question,
                "token", token
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String fullName = payload.get("fullName");
        String phone = payload.get("phone");
        String type = payload.get("type");
        String organization = payload.get("organization");
        String password = payload.get("password");
        String captchaAnswer = payload.get("captchaAnswer");
        String captchaToken = payload.get("captchaToken");

        // 1. Validations de base
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email et mot de passe requis."));
        }

        // 2. Vérification Humaine (Captcha Serveur)
        if (captchaAnswer == null || captchaToken == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vérification humaine manquante."));
        }
        String salt = "KikiSecretSalt2024";
        String expectedToken = org.springframework.util.DigestUtils.md5DigestAsHex((captchaAnswer + salt).getBytes());
        if (!expectedToken.equals(captchaToken)) {
            return ResponseEntity.badRequest().body(Map.of("message", "La vérification humaine a échoué. Mauvaise réponse mathématique."));
        }

        // 3. Validation format de l'email
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le format de l'email est invalide."));
        }

        // 4. Validation de la force du mot de passe
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre."));
        }

        if (userRepository.existsByUsername(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Un compte existe déjà avec cet email."));
        }

        // Créer le client
        Client client = clientService.getOrCreateClient(email, fullName, phone, type, organization);

        // Créer le compte AppUser lié
        AppUser user = AppUser.builder()
                .username(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName != null ? fullName : email)
                .role(UserRole.CLIENT)
                .tempPasswordChangeRequired(false)
                .clientId(client.getId())
                .active(true)
                .build();
        AppUser savedUser = userRepository.save(user);

        // Liaison bidirectionnelle client ↔ AppUser
        clientService.linkUserToClient(client.getId(), savedUser.getId());


        log.info("Nouveau compte client MyKiki créé pour : {}", email);
        return ResponseEntity.ok(Map.of(
                "message", "Compte créé avec succès.",
                "clientId", client.getId()
        ));
    }

    // =============================================
    // PROFIL CLIENT
    // =============================================

    @GetMapping("/profil")
    public ResponseEntity<?> getProfil(@AuthenticationPrincipal AppUser user) {
        if (user.getClientId() == null) {
            return ResponseEntity.ok(Map.of("fullName", user.getFullName(), "username", user.getUsername()));
        }
        Optional<Client> client = clientRepository.findById(user.getClientId());
        return client.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profil")
    public ResponseEntity<?> updateProfil(
            @AuthenticationPrincipal AppUser user,
            @RequestBody Map<String, String> payload) {
        if (user.getClientId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aucun profil client associé."));
        }
        return clientRepository.findById(user.getClientId()).map(client -> {
            if (payload.containsKey("name")) { client.setName(payload.get("name")); client.setNameCol(payload.get("name")); }
            if (payload.containsKey("phone")) { client.setPhone(payload.get("phone")); client.setTelephone(payload.get("phone")); }
            if (payload.containsKey("organization")) client.setOrganization(payload.get("organization"));
            clientRepository.save(client);
            return ResponseEntity.ok(client);
        }).orElse(ResponseEntity.notFound().build());
    }

    // =============================================
    // PROPOSITIONS DU CLIENT
    // =============================================

    @GetMapping("/propositions")
    public ResponseEntity<List<PropositionEnvoyee>> getPropositions(
            @AuthenticationPrincipal AppUser user) {
        if (user.getClientId() == null) return ResponseEntity.ok(List.of());

        // Récupérer les demandes du client
        Optional<Client> client = clientRepository.findById(user.getClientId());
        if (client.isEmpty()) return ResponseEntity.ok(List.of());

        List<DemandeDevis> demandes = demandeDevisRepository.findAllByClientId(user.getClientId());
        List<Long> demandeIds = demandes.stream().map(DemandeDevis::getId).toList();

        if (demandeIds.isEmpty()) return ResponseEntity.ok(List.of());

        List<PropositionEnvoyee> props = propositionEnvoyeeRepository.findByDemandeIdIn(demandeIds);
        return ResponseEntity.ok(props);
    }

    @PostMapping("/propositions/{id}/valider")
    public ResponseEntity<?> validerProposition(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user,
            @RequestBody Map<String, String> payload) {

        return propositionEnvoyeeRepository.findById(id).map(prop -> {
            prop.setStatus("selectionnee_client");
            prop.setClientComment(payload.get("clientComment"));
            prop.setClientSelection(payload.get("clientSelection"));
            prop.setClientValidatedAt(LocalDateTime.now());
            propositionEnvoyeeRepository.save(prop);

            // Notifier le gestionnaire
            String clientName = user.getFullName();
            notificationService.selectionClient(prop.getDemandeId(), clientName);

            log.info("Client {} a validé la proposition #{}", clientName, id);
            return ResponseEntity.ok(Map.of("message", "Votre sélection a été envoyée au traiteur."));
        }).orElse(ResponseEntity.notFound().build());
    }

    // =============================================
    // DEMANDES DU CLIENT
    // =============================================

    @GetMapping("/demandes")
    public ResponseEntity<List<DemandeDevis>> getDemandes(@AuthenticationPrincipal AppUser user) {
        if (user.getClientId() == null) return ResponseEntity.ok(List.of());
        List<DemandeDevis> demandes = demandeDevisRepository.findAllByClientId(user.getClientId());
        return ResponseEntity.ok(demandes);
    }

    // =============================================
    // SWITCH PROFIL (particulier <-> entreprise)
    // =============================================

    @PostMapping("/switch-profil")
    public ResponseEntity<?> switchProfil(
            @AuthenticationPrincipal AppUser user,
            @RequestBody Map<String, String> payload) {
        String newType = payload.get("type"); // "particulier" ou "entreprise"
        if (user.getClientId() == null) return ResponseEntity.badRequest().build();

        return clientRepository.findById(user.getClientId()).map(client -> {
            client.setType(newType);
            clientRepository.save(client);
            return ResponseEntity.ok(Map.of("message", "Profil basculé vers : " + newType));
        }).orElse(ResponseEntity.notFound().build());
    }
}
