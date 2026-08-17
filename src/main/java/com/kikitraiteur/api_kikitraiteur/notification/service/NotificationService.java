package com.kikitraiteur.api_kikitraiteur.notification.service;

import com.kikitraiteur.api_kikitraiteur.notification.model.Notification;
import com.kikitraiteur.api_kikitraiteur.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // =====================================================
    // Constantes de type
    // =====================================================
    public static final String TYPE_NOUVELLE_DEMANDE = "NOUVELLE_DEMANDE";
    public static final String TYPE_PROPOSITION_ENVOYEE = "PROPOSITION_ENVOYEE";
    public static final String TYPE_SELECTION_CLIENT = "SELECTION_CLIENT";
    public static final String TYPE_DEMANDE_ABOUTIE = "DEMANDE_ABOUTIE";
    public static final String TYPE_DEMANDE_REFUSEE = "DEMANDE_REFUSEE";

    /**
     * Crée une notification pour un rôle entier (ex: tous les GESTIONNAIRE/ADMIN).
     */
    @Transactional
    public Notification notifyRole(String type, String targetRole, String message, Long demandeId) {
        Notification notif = Notification.builder()
                .type(type)
                .targetRole(targetRole)
                .message(message)
                .demandeId(demandeId)
                .lu(false)
                .build();
        Notification saved = notificationRepository.save(notif);
        log.info("Notification [{}] créée pour le rôle {} : {}", type, targetRole, message);
        return saved;
    }

    /**
     * Crée une notification pour un utilisateur spécifique (ex: client).
     */
    @Transactional
    public Notification notifyUser(String type, Long targetUserId, String message,
                                   Long demandeId, Long propositionEnvoyeeId) {
        Notification notif = Notification.builder()
                .type(type)
                .targetUserId(targetUserId)
                .message(message)
                .demandeId(demandeId)
                .propositionEnvoyeeId(propositionEnvoyeeId)
                .lu(false)
                .build();
        Notification saved = notificationRepository.save(notif);
        log.info("Notification [{}] créée pour l'utilisateur #{} : {}", type, targetUserId, message);
        return saved;
    }

    /**
     * Récupère les notifications non lues pour un utilisateur connecté.
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnread(Long userId, String role) {
        return notificationRepository.findUnreadByUserIdOrRole(userId, role);
    }

    /**
     * Compte les notifications non lues (pour le badge).
     */
    @Transactional(readOnly = true)
    public Map<String, Long> countUnread(Long userId, String role) {
        long count = notificationRepository.countUnreadByUserIdOrRole(userId, role);
        return Map.of("count", count);
    }

    /**
     * Marque une notification comme lue.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
    }

    /**
     * Marque toutes les notifications non lues de l'utilisateur comme lues.
     */
    @Transactional
    public void markAllAsRead(Long userId, String role) {
        List<Notification> unread = notificationRepository.findUnreadByUserIdOrRole(userId, role);
        unread.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(unread);
    }

    // =====================================================
    // Helpers métier — appelés depuis les services
    // =====================================================

    /** Notifie les gestionnaires lors d'une nouvelle demande. */
    public void nouvelleDemande(Long demandeId, String clientName) {
        notifyRole(TYPE_NOUVELLE_DEMANDE, "GESTIONNAIRE",
                "📩 Nouvelle demande reçue de " + clientName, demandeId);
        notifyRole(TYPE_NOUVELLE_DEMANDE, "ADMIN",
                "📩 Nouvelle demande reçue de " + clientName, demandeId);
    }

    /** Notifie le client quand le gestionnaire envoie une proposition. */
    public void propositionEnvoyee(Long clientUserId, Long demandeId, Long propositionEnvoyeeId) {
        notifyUser(TYPE_PROPOSITION_ENVOYEE, clientUserId,
                "📋 Le traiteur vous a envoyé une proposition commerciale. Consultez-la dans votre espace MyKiki.",
                demandeId, propositionEnvoyeeId);
    }

    /** Notifie les gestionnaires quand un client valide une proposition. */
    public void selectionClient(Long demandeId, String clientName) {
        notifyRole(TYPE_SELECTION_CLIENT, "GESTIONNAIRE",
                "✅ " + clientName + " a validé sa sélection sur une proposition. En attente de votre confirmation.",
                demandeId);
        notifyRole(TYPE_SELECTION_CLIENT, "ADMIN",
                "✅ " + clientName + " a validé sa sélection sur une proposition.",
                demandeId);
    }

    /** Notifie toutes les parties quand une demande est aboutie. */
    public void demandeAboutie(Long demandeId, Long clientUserId, String clientName) {
        notifyRole(TYPE_DEMANDE_ABOUTIE, "GESTIONNAIRE",
                "🎉 La demande de " + clientName + " est maintenant aboutie ! L'événement a été créé dans le calendrier.",
                demandeId);
        if (clientUserId != null) {
            notifyUser(TYPE_DEMANDE_ABOUTIE, clientUserId,
                    "🎉 Votre demande a été confirmée ! L'équipe Kiki Traiteur vous contactera prochainement pour les derniers détails.",
                    demandeId, null);
        }
    }
}
