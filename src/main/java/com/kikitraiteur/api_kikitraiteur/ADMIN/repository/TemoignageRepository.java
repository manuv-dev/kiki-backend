package com.kikitraiteur.api_kikitraiteur.ADMIN.repository;

import com.kikitraiteur.api_kikitraiteur.ADMIN.model.Temoignage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemoignageRepository extends JpaRepository<Temoignage, Long> {
}
