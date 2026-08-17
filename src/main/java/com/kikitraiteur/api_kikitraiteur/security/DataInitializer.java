package com.kikitraiteur.api_kikitraiteur.security;

import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import com.kikitraiteur.api_kikitraiteur.auth.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Initialise la base de données au premier démarrage UNIQUEMENT si aucun admin n'existe.
 * Crée :
 * - L'administrateur principal : NYADZI Emmanuel
 * - 2 gestionnaires de test
 * - 3 personnels de test
 *
 * ⚠️ Les mots de passe sont loggués UNE SEULE FOIS dans la console au démarrage.
 *    Cherchez la ligne commençant par "[INIT]" dans les logs.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Vérifier si un admin existe déjà — ne rien faire si oui
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            log.info("DataInitializer : Admin déjà présent. Initialisation ignorée.");
            return;
        }

        log.info("DataInitializer : Première initialisation de la base de données...");

        // ==========================================
        // 1. ADMINISTRATEUR PRINCIPAL
        // ==========================================
        String adminPassword = "KikiAdmin@2026!Secure";
        String adminSlug = "admin-kiki-secure-" + UUID.randomUUID().toString().substring(0, 8);

        AppUser admin = AppUser.builder()
                .username("nyadzi.admin")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .fullName("NYADZI Emmanuel")
                .role(UserRole.ADMIN)
                .tempPasswordChangeRequired(false)
                .customLoginSlug(adminSlug)
                .active(true)
                .build();
        userRepository.save(admin);

        // ==========================================
        // 2. GESTIONNAIRES DE TEST
        // ==========================================
        String gest1Pass = generateTempPassword();
        String gest1Slug = "gestionnaire-marie-" + UUID.randomUUID().toString().substring(0, 8);
        AppUser gest1 = AppUser.builder()
                .username("marie.gestionnaire")
                .passwordHash(passwordEncoder.encode(gest1Pass))
                .fullName("Marie Diallo")
                .role(UserRole.GESTIONNAIRE)
                .tempPasswordChangeRequired(true)
                .customLoginSlug(gest1Slug)
                .active(true)
                .build();
        userRepository.save(gest1);

        String gest2Pass = generateTempPassword();
        String gest2Slug = "gestionnaire-jean-" + UUID.randomUUID().toString().substring(0, 8);
        AppUser gest2 = AppUser.builder()
                .username("jean.gestionnaire")
                .passwordHash(passwordEncoder.encode(gest2Pass))
                .fullName("Jean-Pierre Sow")
                .role(UserRole.GESTIONNAIRE)
                .tempPasswordChangeRequired(true)
                .customLoginSlug(gest2Slug)
                .active(true)
                .build();
        userRepository.save(gest2);

        // ==========================================
        // 3. PERSONNELS DE TEST
        // ==========================================
        String[] personnelNames = {"Fatou Dieng", "Moussa Ndiaye", "Aïssatou Fall"};
        String[] personnelUsernames = {"fatou.cuisine", "moussa.service", "aissatou.coord"};
        String[] personnelPasswords = new String[3];

        for (int i = 0; i < personnelNames.length; i++) {
            personnelPasswords[i] = generateTempPassword();
            AppUser personnel = AppUser.builder()
                    .username(personnelUsernames[i])
                    .passwordHash(passwordEncoder.encode(personnelPasswords[i]))
                    .fullName(personnelNames[i])
                    .role(UserRole.PERSONNEL)
                    .tempPasswordChangeRequired(true)
                    .active(true)
                    .build();
            userRepository.save(personnel);
        }

        // ==========================================
        // AFFICHAGE DES IDENTIFIANTS — UNE SEULE FOIS
        // ==========================================
        log.info("");
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║          [INIT] IDENTIFIANTS DE CONNEXION KIKI TRAITEUR       ║");
        log.info("╠═══════════════════════════════════════════════════════════════╣");
        log.info("║  ⚠️  Ces informations ne seront JAMAIS réaffichées !          ║");
        log.info("║  Copiez-les maintenant et stockez-les en lieu sûr.           ║");
        log.info("╠═══════════════════════════════════════════════════════════════╣");
        log.info("║  ADMINISTRATEUR                                               ║");
        log.info("║  Nom       : NYADZI Emmanuel                                 ║");
        log.info("║  Username  : nyadzi.admin                                    ║");
        log.info("║  Password  : {}                         ║", adminPassword);
        log.info("║  Slug URL  : /login/{}  ║", adminSlug);
        log.info("╠═══════════════════════════════════════════════════════════════╣");
        log.info("║  GESTIONNAIRE 1 — Marie Diallo                               ║");
        log.info("║  Username  : marie.gestionnaire                              ║");
        log.info("║  Password  : {} (temporaire)              ║", gest1Pass);
        log.info("║  Slug URL  : /login/{}     ║", gest1Slug);
        log.info("╠═══════════════════════════════════════════════════════════════╣");
        log.info("║  GESTIONNAIRE 2 — Jean-Pierre Sow                            ║");
        log.info("║  Username  : jean.gestionnaire                               ║");
        log.info("║  Password  : {} (temporaire)              ║", gest2Pass);
        log.info("║  Slug URL  : /login/{}      ║", gest2Slug);
        log.info("╠═══════════════════════════════════════════════════════════════╣");
        log.info("║  PERSONNELS (doivent changer leur mot de passe à la 1ère connexion) ║");
        for (int i = 0; i < personnelNames.length; i++) {
            log.info("║  {} — {} : {}  ║", personnelNames[i], personnelUsernames[i], personnelPasswords[i]);
        }
        log.info("╚═══════════════════════════════════════════════════════════════╝");
        log.info("");
    }

    private String generateTempPassword() {
        return "Kiki@" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
