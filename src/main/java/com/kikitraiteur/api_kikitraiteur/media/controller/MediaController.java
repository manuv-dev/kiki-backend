package com.kikitraiteur.api_kikitraiteur.media.controller;

import com.kikitraiteur.api_kikitraiteur.media.model.Media;
import com.kikitraiteur.api_kikitraiteur.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/gestionnaire/media")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
public class MediaController {

    private final MediaRepository mediaRepository;

    @Value("${media.upload.dir:uploads/media}")
    private String uploadDir;

    /** Liste des médias (optionnel: filtrer par événement) */
    @GetMapping
    public ResponseEntity<List<Media>> getAll(
            @RequestParam(required = false) Long evenementId) {
        List<Media> medias;
        if (evenementId != null) {
            medias = mediaRepository.findByEvenementIdOrderByUploadedAtDesc(evenementId);
        } else {
            medias = mediaRepository.findAllByOrderByUploadedAtDesc();
        }
        return ResponseEntity.ok(medias);
    }

    /**
     * Upload en groupe — plusieurs fichiers rattachés à un événement.
     * Tous les fichiers sont stockés et regroupés par evenementId.
     */
    @PostMapping("/upload")
    public ResponseEntity<List<Media>> upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) Long evenementId,
            @RequestParam(required = false) String evenementNom
    ) {
        List<Media> uploaded = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Sous-dossier par événement
            if (evenementId != null) {
                uploadPath = uploadPath.resolve("event_" + evenementId);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();
                String extension = originalName != null && originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : "";
                String uniqueName = UUID.randomUUID() + extension;

                Path filePath = uploadPath.resolve(uniqueName);
                Files.copy(file.getInputStream(), filePath);

                String type = detectType(file.getContentType());
                String urlPath = "/uploads/media/" +
                        (evenementId != null ? "event_" + evenementId + "/" : "") +
                        uniqueName;

                Media media = Media.builder()
                        .nom(originalName != null ? originalName : uniqueName)
                        .url(urlPath)
                        .type(type)
                        .sizeBytes(file.getSize())
                        .evenementId(evenementId)
                        .evenementNom(evenementNom)
                        .build();

                uploaded.add(mediaRepository.save(media));
                log.info("Fichier uploadé : {} pour événement #{}", originalName, evenementId);
            }

            return ResponseEntity.ok(uploaded);

        } catch (IOException e) {
            log.error("Erreur upload média : {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Supprimer un média */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        mediaRepository.findById(id).ifPresent(m -> {
            // Supprimer le fichier physique
            try {
                Path file = Paths.get(m.getUrl().replace("/uploads/media", uploadDir));
                Files.deleteIfExists(file);
            } catch (IOException e) {
                log.warn("Impossible de supprimer le fichier physique : {}", e.getMessage());
            }
            mediaRepository.delete(m);
        });
        return ResponseEntity.ok(Map.of("message", "Média supprimé."));
    }

    private String detectType(String contentType) {
        if (contentType == null) return "DOCUMENT";
        if (contentType.startsWith("image/")) return "IMAGE";
        if (contentType.startsWith("video/")) return "VIDEO";
        if (contentType.equals("application/pdf")) return "PDF";
        return "DOCUMENT";
    }
}
