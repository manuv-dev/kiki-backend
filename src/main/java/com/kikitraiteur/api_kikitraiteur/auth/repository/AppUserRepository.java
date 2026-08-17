package com.kikitraiteur.api_kikitraiteur.auth.repository;

import com.kikitraiteur.api_kikitraiteur.auth.model.AppUser;
import com.kikitraiteur.api_kikitraiteur.auth.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByCustomLoginSlug(String slug);

    boolean existsByRole(UserRole role);

    boolean existsByUsername(String username);

    List<AppUser> findAllByRole(UserRole role);

    List<AppUser> findAllByRoleIn(List<UserRole> roles);
}
