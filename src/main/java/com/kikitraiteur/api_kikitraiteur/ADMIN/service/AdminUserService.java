package com.kikitraiteur.api_kikitraiteur.ADMIN.service;

import com.kikitraiteur.api_kikitraiteur.ADMIN.dto.AdminCreateUserRequest;
import com.kikitraiteur.api_kikitraiteur.ADMIN.dto.AdminUpdateUserRequest;
import com.kikitraiteur.api_kikitraiteur.ADMIN.dto.AdminUserResponse;
import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import com.kikitraiteur.api_kikitraiteur.auth.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public List<AdminUserResponse> getAllStaff() {
        return userRepository.findAllByRoleIn(List.of(UserRole.ADMIN, UserRole.GESTIONNAIRE, UserRole.PERSONNEL))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminUserResponse createStaffUser(AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        if (request.getRole() == UserRole.CLIENT) {
            throw new IllegalArgumentException("Les clients ne peuvent pas être créés via ce panneau.");
        }

        String tempPassword = generateRandomPassword();
        String slug = request.getRole().name().toLowerCase() + "-kiki-" + UUID.randomUUID().toString().substring(0, 8);

        AppUser user = AppUser.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .role(request.getRole())
                .customLoginSlug(slug)
                .tempPasswordChangeRequired(true) // L'utilisateur devra changer son mot de passe
                .active(true)
                .build();

        user = userRepository.save(user);

        AdminUserResponse response = mapToResponse(user);
        response.setTempPassword(tempPassword); // On retourne le mot de passe généré 1 seule fois
        return response;
    }

    @Transactional
    public AdminUserResponse updateStaffUser(Long id, AdminUpdateUserRequest request) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        // On ne permet pas de modifier son propre statut actif si on est le seul admin
        if (user.getRole() == UserRole.ADMIN && !request.isActive()) {
             // Basic protection, we could do more checks
        }

        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        user.setActive(request.isActive());

        user = userRepository.save(user);
        return mapToResponse(user);
    }
    
    @Transactional
    public void deleteStaffUser(Long id) {
         AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
         if (user.getRole() == UserRole.ADMIN) {
             throw new IllegalArgumentException("Impossible de supprimer un administrateur.");
         }
         userRepository.delete(user);
    }

    private AdminUserResponse mapToResponse(AppUser user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .customLoginSlug(user.getCustomLoginSlug())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .loginUrl(user.getCustomLoginSlug() != null ? frontendUrl + "/login/" + user.getCustomLoginSlug() : null)
                .build();
    }

    private String generateRandomPassword() {
        return "Kiki@" + UUID.randomUUID().toString().substring(0, 6) + "!";
    }
}
