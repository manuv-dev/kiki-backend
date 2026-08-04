package com.kikitraiteur.api_kikitraiteur.ADMIN.repository;

import com.kikitraiteur.api_kikitraiteur.ADMIN.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
}
