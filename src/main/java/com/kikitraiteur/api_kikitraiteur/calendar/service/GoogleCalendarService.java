package com.kikitraiteur.api_kikitraiteur.calendar.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.kikitraiteur.api_kikitraiteur.calendar.model.CalendarEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service de synchronisation bidirectionnelle avec Google Calendar.
 *
 * Configuration requise dans .env :
 * GOOGLE_CLIENT_ID=votre-client-id.apps.googleusercontent.com
 * GOOGLE_CLIENT_SECRET=votre-secret
 * GOOGLE_CALENDAR_ID=primary (ou ID de l'agenda partagé)
 * GOOGLE_TOKENS_DIR=tokens (dossier de stockage des tokens OAuth2)
 */
@Service
@Slf4j
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Kiki Traiteur Calendar";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TIMEZONE = "Africa/Dakar";

    @Value("${google.calendar.client-id:}")
    private String clientId;

    @Value("${google.calendar.client-secret:}")
    private String clientSecret;

    @Value("${google.calendar.calendar-id:primary}")
    private String calendarId;

    @Value("${google.calendar.tokens-dir:tokens}")
    private String tokensDir;

    private boolean isConfigured() {
        return clientId != null && !clientId.isBlank() &&
               clientSecret != null && !clientSecret.isBlank();
    }

    private Calendar buildCalendarService() throws GeneralSecurityException, IOException {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        String clientSecretsJson = """
                {"installed":{"client_id":"%s","client_secret":"%s",
                "redirect_uris":["urn:ietf:wg:oauth:2.0:oob","http://localhost"],
                "auth_uri":"https://accounts.google.com/o/oauth2/auth",
                "token_uri":"https://oauth2.googleapis.com/token"}}
                """.formatted(clientId, clientSecret);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new StringReader(clientSecretsJson));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singletonList(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDir)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Synchronise un événement Kiki vers Google Calendar.
     * @return l'ID Google de l'événement créé/mis à jour
     */
    public Optional<String> syncEventToGoogle(CalendarEvent kikiEvent) {
        if (!isConfigured()) {
            log.warn("Google Calendar non configuré. Synchronisation ignorée pour l'événement #{}", kikiEvent.getId());
            return Optional.empty();
        }

        try {
            Calendar service = buildCalendarService();
            Event googleEvent = toGoogleEvent(kikiEvent);

            Event result;
            if (kikiEvent.getGoogleEventId() != null && !kikiEvent.getGoogleEventId().isBlank()) {
                // Mise à jour d'un événement existant
                result = service.events().update(calendarId, kikiEvent.getGoogleEventId(), googleEvent).execute();
                log.info("Événement Google Calendar mis à jour : {}", result.getId());
            } else {
                // Création d'un nouvel événement
                result = service.events().insert(calendarId, googleEvent).execute();
                log.info("Événement créé dans Google Calendar : {}", result.getId());
            }

            return Optional.of(result.getId());

        } catch (Exception e) {
            log.error("Erreur synchronisation Google Calendar pour événement #{}: {}",
                    kikiEvent.getId(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Supprime un événement de Google Calendar.
     */
    public void deleteFromGoogle(String googleEventId) {
        if (!isConfigured() || googleEventId == null) return;

        try {
            Calendar service = buildCalendarService();
            service.events().delete(calendarId, googleEventId).execute();
            log.info("Événement supprimé de Google Calendar : {}", googleEventId);
        } catch (Exception e) {
            log.error("Erreur suppression Google Calendar événement {}: {}", googleEventId, e.getMessage());
        }
    }

    /**
     * Convertit un CalendarEvent Kiki en Event Google.
     */
    private Event toGoogleEvent(CalendarEvent kikiEvent) {
        Event event = new Event();
        event.setSummary("🍽️ " + kikiEvent.getTitre());

        // Description enrichie
        StringBuilder description = new StringBuilder();
        description.append("Client : ").append(kikiEvent.getClientName() != null ? kikiEvent.getClientName() : "N/A").append("\n");
        description.append("Convives : ").append(kikiEvent.getNombreConvives() != null ? kikiEvent.getNombreConvives() : "N/A").append("\n");
        description.append("Lieu : ").append(kikiEvent.getLieu() != null ? kikiEvent.getLieu() : "N/A").append("\n");
        if (kikiEvent.getResponsable() != null) description.append("Responsable : ").append(kikiEvent.getResponsable()).append("\n");
        if (kikiEvent.getNotes() != null) description.append("\nNotes : ").append(kikiEvent.getNotes());
        event.setDescription(description.toString());

        if (kikiEvent.getLieu() != null) {
            event.setLocation(kikiEvent.getLieu());
        }

        // Date de début
        if (kikiEvent.getHeureDebut() != null) {
            ZonedDateTime start = kikiEvent.getDateDebut().atTime(kikiEvent.getHeureDebut())
                    .atZone(ZoneId.of(TIMEZONE));
            event.setStart(new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(start.toInstant().toEpochMilli()))
                    .setTimeZone(TIMEZONE));
        } else {
            event.setStart(new EventDateTime()
                    .setDate(new com.google.api.client.util.DateTime(true,
                            kikiEvent.getDateDebut().toEpochDay() * 86400000L, 0)));
        }

        // Date de fin (défaut : +3h si heure définie)
        LocalServerEnd(kikiEvent, event);

        return event;
    }

    private void LocalServerEnd(CalendarEvent kikiEvent, Event event) {
        if (kikiEvent.getHeureFin() != null) {
            ZonedDateTime end = kikiEvent.getDateDebut()
                    .atTime(kikiEvent.getHeureFin())
                    .atZone(ZoneId.of(TIMEZONE));
            event.setEnd(new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(end.toInstant().toEpochMilli()))
                    .setTimeZone(TIMEZONE));
        } else if (kikiEvent.getHeureDebut() != null) {
            ZonedDateTime end = kikiEvent.getDateDebut()
                    .atTime(kikiEvent.getHeureDebut().plusHours(3))
                    .atZone(ZoneId.of(TIMEZONE));
            event.setEnd(new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(end.toInstant().toEpochMilli()))
                    .setTimeZone(TIMEZONE));
        } else {
            LocalDate endDate = kikiEvent.getDateFin() != null ? kikiEvent.getDateFin() : kikiEvent.getDateDebut();
            event.setEnd(new EventDateTime()
                    .setDate(new com.google.api.client.util.DateTime(true,
                            endDate.toEpochDay() * 86400000L, 0)));
        }
    }
}
