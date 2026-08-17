package com.kikitraiteur.api_kikitraiteur.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "app_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"custom_login_slug"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login (ex: nyadzi.admin, gestionnaire1@kiki, ou email client) */
    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Si true, l'utilisateur doit changer son mot de passe à la prochaine connexion.
     * Utilisé pour les gestionnaires créés par l'admin.
     */
    @Column(name = "temp_password_change_required", nullable = false)
    @Builder.Default
    private boolean tempPasswordChangeRequired = false;

    /**
     * Slug URL personnalisé pour le lien de connexion sécurisé.
     * Ex: "admin-secure-abc123" → accessible via /login/admin-secure-abc123
     * Nullable : uniquement pour ADMIN et GESTIONNAIRE.
     */
    @Column(name = "custom_login_slug", unique = true)
    private String customLoginSlug;

    /** ID du client lié (si rôle CLIENT) */
    @Column(name = "client_id")
    private Long clientId;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ---- UserDetails interface ----

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }
}
