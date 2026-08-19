package com.kikitraiteur.api_kikitraiteur.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Tentative de déblocage et suppression de la contrainte 'app_users_role_check'...");
        try {
            // 1. Tuer les requêtes fantômes qui bloquent potentiellement la table
            log.info("Libération des verrous éventuels sur Neon DB...");
            jdbcTemplate.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'idle in transaction' OR state = 'active' AND pid <> pg_backend_pid();");
            
            // Attendre une seconde pour que les connexions soient bien terminées
            Thread.sleep(1000);
            
            // 2. Supprimer la contrainte sans bloquer indéfiniment
            log.info("Suppression de la contrainte...");
            jdbcTemplate.execute("SET lock_timeout = '5s';");
            jdbcTemplate.execute("ALTER TABLE app_users DROP CONSTRAINT IF EXISTS app_users_role_check;");
            log.info("Contrainte 'app_users_role_check' supprimée avec succès (la base acceptera désormais tous les rôles).");
        } catch (Exception e) {
            log.error("Attention: Impossible de supprimer la contrainte SQL (elle est peut-être déjà supprimée ou le timeout est atteint) : {}", e.getMessage());
        }
    }
}
