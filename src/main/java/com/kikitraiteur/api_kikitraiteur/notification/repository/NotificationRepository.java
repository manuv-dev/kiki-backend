package com.kikitraiteur.api_kikitraiteur.notification.repository;

import com.kikitraiteur.api_kikitraiteur.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Récupère les notifications non lues pour un userId précis OU pour un rôle cible */
    @Query("SELECT n FROM Notification n WHERE n.lu = false AND " +
           "(n.targetUserId = :userId OR n.targetRole = :role) " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserIdOrRole(@Param("userId") Long userId, @Param("role") String role);

    /** Compte les non lues (pour badge) */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.lu = false AND " +
           "(n.targetUserId = :userId OR n.targetRole = :role)")
    long countUnreadByUserIdOrRole(@Param("userId") Long userId, @Param("role") String role);

    /** Toutes les notifications non lues pour un rôle (sans userId spécifique) */
    List<Notification> findByTargetRoleAndLuFalseOrderByCreatedAtDesc(String targetRole);
}
