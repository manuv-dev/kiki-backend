package com.kikitraiteur.api_kikitraiteur.notification.controller;

import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.notification.model.Notification;
import com.kikitraiteur.api_kikitraiteur.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /** Liste des notifications non lues de l'utilisateur connecté */
    @GetMapping
    public ResponseEntity<List<Notification>> getUnread(@AuthenticationPrincipal AppUser user) {
        String role = user.getRole().name();
        List<Notification> notifications = notificationService.getUnread(user.getId(), role);
        return ResponseEntity.ok(notifications);
    }

    /** Nombre de notifications non lues (pour le badge) */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countUnread(@AuthenticationPrincipal AppUser user) {
        String role = user.getRole().name();
        return ResponseEntity.ok(notificationService.countUnread(user.getId(), role));
    }

    /** Marque une notification spécifique comme lue */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /** Marque toutes les notifications comme lues */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal AppUser user) {
        notificationService.markAllAsRead(user.getId(), user.getRole().name());
        return ResponseEntity.ok().build();
    }
}
