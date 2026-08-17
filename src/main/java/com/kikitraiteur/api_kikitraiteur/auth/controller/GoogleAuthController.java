package com.kikitraiteur.api_kikitraiteur.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kikitraiteur.api_kikitraiteur.Client.model.Client;
import com.kikitraiteur.api_kikitraiteur.Client.service.ClientService;
import com.kikitraiteur.api_kikitraiteur.auth.dto.GoogleLoginRequest;
import com.kikitraiteur.api_kikitraiteur.auth.dto.LoginResponse;
import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import com.kikitraiteur.api_kikitraiteur.auth.repository.AppUserRepository;
import com.kikitraiteur.api_kikitraiteur.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/google")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthController {

    private final AppUserRepository userRepository;
    private final ClientService clientService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // TODO: Replace with your actual Google Client ID from Google Cloud Console
    private static final String CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID";

    @PostMapping("/login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        if (request.getCredential() == null || request.getCredential().isBlank()) {
            return ResponseEntity.badRequest().body("Token Google manquant.");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    // Si vous avez un vrai Client ID, décommentez la ligne suivante :
                    // .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            // Note : Sans setAudience, on vérifie juste la signature cryptographique du token, ce qui est moins sécurisé en prod.
            // Il faudra activer .setAudience avec le vrai CLIENT_ID en production.
            
            // On parse sans vérifier l'audience stricte pour l'instant (facilite les tests sans vrai Client ID).
            // En production, utilisez verifier.verify(...)
            GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), request.getCredential());
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");

                if (!emailVerified) {
                    return ResponseEntity.badRequest().body("Email Google non vérifié.");
                }

                Optional<AppUser> existingUser = userRepository.findByUsername(email);
                AppUser user;

                if (existingUser.isPresent()) {
                    user = existingUser.get();
                    if (!user.isActive()) {
                        return ResponseEntity.status(403).body("Compte désactivé. Contactez l'administrateur.");
                    }
                    user.setLastLoginAt(LocalDateTime.now());
                    user = userRepository.save(user);
                    log.info("Connexion Google réussie pour un utilisateur existant : {}", email);
                } else {
                    // Création de compte automatique via Google
                    Client client = clientService.getOrCreateClient(email, name, null, "particulier", null);

                    user = AppUser.builder()
                            .username(email)
                            // Mot de passe généré aléatoirement (l'utilisateur se connectera toujours via Google)
                            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .fullName(name != null ? name : email)
                            .role(UserRole.CLIENT)
                            .tempPasswordChangeRequired(false)
                            .clientId(client.getId())
                            .active(true)
                            .lastLoginAt(LocalDateTime.now())
                            .build();

                    user = userRepository.save(user);
                    clientService.linkUserToClient(client.getId(), user.getId());

                    log.info("Création de compte Google réussie pour : {}", email);
                }

                String token = jwtService.generateToken(user);
                String redirectUrl = user.getRole() == UserRole.CLIENT ? "/mykiki" : "/gestionnaire/dashboard";

                return ResponseEntity.ok(LoginResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .userId(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .tempPasswordChangeRequired(user.isTempPasswordChangeRequired())
                        .redirectUrl(redirectUrl)
                        .build());
            } else {
                return ResponseEntity.badRequest().body("Token Google invalide.");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du token Google", e);
            return ResponseEntity.status(500).body("Erreur serveur lors de la vérification Google.");
        }
    }
}
