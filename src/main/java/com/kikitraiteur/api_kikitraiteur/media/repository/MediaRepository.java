package com.kikitraiteur.api_kikitraiteur.media.repository;

import com.kikitraiteur.api_kikitraiteur.media.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByEvenementIdOrderByUploadedAtDesc(Long evenementId);
    List<Media> findByEvenementIdIsNullOrderByUploadedAtDesc();
    List<Media> findByTypeOrderByUploadedAtDesc(String type);
    List<Media> findAllByOrderByUploadedAtDesc();
}
