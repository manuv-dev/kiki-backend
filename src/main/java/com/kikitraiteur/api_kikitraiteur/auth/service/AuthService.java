package com.kikitraiteur.api_kikitraiteur.auth.service;

import com.kikitraiteur.api_kikitraiteur.auth.dto.*;
import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import com.kikitraiteur.api_kikitraiteur.auth.repository.AppUserRepository;
import com.kikitraiteur.api_kikitraiteur.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     * Si un slug est fourni, vérifie que l'utilisateur y est bien associé.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        AppUser user;

        // Si connexion via slug personnalisé
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            user = userRepository.findByCustomLoginSlug(request.getSlug())
                    .orElseThrow(() -> new BadCredentialsException("Lien de connexion invalide"));
            // Vérifier le mot de passe
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new BadCredentialsException("Identifiants incorrects");
            }
        } else {
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Utilisateur introuvable"));
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new BadCredentialsException("Identifiants incorrects");
            }
        }

        if (!user.isActive()) {
            throw new BadCredentialsException("Compte désactivé. Contactez l'administrateur.");
        }

        // Mettre à jour lastLoginAt
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        String redirectUrl = switch (user.getRole()) {
            case ADMIN, GESTIONNAIRE -> "/gestionnaire/dashboard";
            case PERSONNEL,
                 RESPONSABLE_CUISINE, SOUS_CHEF, ECONOME, MAGASINIER,
                 CONTROLEUR, CUISINIER, SERVEUR, AIDE_CUISINIER,
                 CHAUFFEUR, PLONGEUR, AGENT_SECURITE -> "/gestionnaire/dashboard";
            case CLIENT -> "/mykiki";
        };

        log.info("Connexion réussie pour '{}' (rôle: {})", user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tempPasswordChangeRequired(user.isTempPasswordChangeRequired())
                .redirectUrl(redirectUrl)
                .build();
    }

    /**
     * Change le mot de passe d'un utilisateur connecté.
     * Si tempPasswordChangeRequired=true, le désactive après le changement.
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifier l'ancien mot de passe sauf si c'est un changement forcé
        if (!user.isTempPasswordChangeRequired()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new BadCredentialsException("Mot de passe actuel incorrect");
            }
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setTempPasswordChangeRequired(false);
        userRepository.save(user);
        log.info("Mot de passe changé pour '{}'", username);
    }

    /**
     * Crée un nouvel utilisateur (gestionnaire, personnel, client).
     * Réservé à l'ADMIN.
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Le nom d'utilisateur '" + request.getUsername() + "' est déjà pris.");
        }

        // Générer un mot de passe temporaire si non fourni
        String rawPassword = (request.getTemporaryPassword() != null && !request.getTemporaryPassword().isBlank())
                ? request.getTemporaryPassword()
                : generateTempPassword();

        // Générer un slug pour admin et gestionnaire
        String slug = null;
        if (request.getRole() == UserRole.ADMIN || request.getRole() == UserRole.GESTIONNAIRE) {
            slug = (request.getCustomLoginSlug() != null && !request.getCustomLoginSlug().isBlank())
                    ? request.getCustomLoginSlug()
                    : generateSlug(request.getRole(), request.getFullName());
        }

        AppUser newUser = AppUser.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .fullName(request.getFullName())
                .role(request.getRole())
                .tempPasswordChangeRequired(request.getRole() != UserRole.ADMIN)
                .customLoginSlug(slug)
                .active(true)
                .build();

        AppUser saved = userRepository.save(newUser);

        log.info("=== COMPTE CRÉÉ ===");
        log.info("Utilisateur : {} ({})", saved.getUsername(), saved.getRole());
        log.info("Mot de passe temporaire : {}", rawPassword);
        if (slug != null) log.info("Lien personnalisé : /login/{}", slug);
        log.info("===================");

        return toDto(saved);
    }

    /**
     * Réinitialise l'accès d'un gestionnaire (nouveau mot de passe temporaire).
     */
    @Transactional
    public String resetAccess(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String newPassword = generateTempPassword();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setTempPasswordChangeRequired(true);
        userRepository.save(user);

        log.info("Accès réinitialisé pour '{}'. Nouveau mot de passe : {}", user.getUsername(), newPassword);
        return newPassword;
    }

    /** Active ou désactive un compte. */
    @Transactional
    public void toggleActive(Long userId, boolean active) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setActive(active);
        userRepository.save(user);
    }

    /** Liste de tous les utilisateurs (hors ADMIN si appelant est GESTIONNAIRE). */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Retourne le profil de l'utilisateur connecté. */
    @Transactional(readOnly = true)
    public UserDto getProfile(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return toDto(user);
    }

    // --- Helpers ---

    private String generateTempPassword() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "Kiki@" + uuid.substring(0, 8).toUpperCase();
    }

    private String generateSlug(UserRole role, String fullName) {
        String base = role.name().toLowerCase() + "-" +
                fullName.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "") + "-" +
                UUID.randomUUID().toString().substring(0, 8);
        return base;
    }

    public UserDto toDto(AppUser user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tempPasswordChangeRequired(user.isTempPasswordChangeRequired())
                .active(user.isActive())
                .customLoginSlug(user.getCustomLoginSlug())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
