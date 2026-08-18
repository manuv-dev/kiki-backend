package com.kikitraiteur.api_kikitraiteur.ADMIN.controller;

import com.kikitraiteur.api_kikitraiteur.ADMIN.dto.AdminCreateUserRequest;
import com.kikitraiteur.api_kikitraiteur.ADMIN.dto.AdminUpdateUserRequest;
import com.kikitraiteur.api_kikitraiteur.ADMIN.dto.AdminUserResponse;
import com.kikitraiteur.api_kikitraiteur.ADMIN.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllStaff() {
        return ResponseEntity.ok(adminUserService.getAllStaff());
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createStaffUser(@RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.ok(adminUserService.createStaffUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> updateStaffUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(adminUserService.updateStaffUser(id, request));
    }

    /**
     * Réinitialise l'accès d'un utilisateur :
     * nouveau mot de passe temporaire généré, obligé de changer à la prochaine connexion.
     */
    @PostMapping("/{id}/reset-access")
    public ResponseEntity<AdminUserResponse> resetAccess(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.resetAccess(id));
    }

    /**
     * Active ou désactive un compte utilisateur.
     * Query param: active=true|false
     */
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<AdminUserResponse> toggleActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(adminUserService.toggleActive(id, active));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffUser(@PathVariable Long id) {
        adminUserService.deleteStaffUser(id);
        return ResponseEntity.noContent().build();
    }
}
