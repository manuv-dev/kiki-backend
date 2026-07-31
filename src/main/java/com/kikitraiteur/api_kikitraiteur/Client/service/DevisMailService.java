package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.kikitraiteur.api_kikitraiteur.Client.model.Devis;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DevisMailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final DevisPdfService pdfService;

    public DevisMailService(DevisPdfService pdfService) {
        this.pdfService = pdfService;
    }

    public void sendDevisEmailWithPdf(Devis devis) {
        String recipient = devis.getClientEmail();
        if (recipient == null || recipient.trim().isEmpty() || "client@gmail.com".equalsIgnoreCase(recipient)) {
            log.info("Envoi de mail simulé pour le devis Réf: {} (email destinataire non spécifié ou générique: {}) - expéditeur: contact@kikitraiteursenegal.net", 
                     devis.getDevisRef(), recipient);
            return;
        }

        try {
            byte[] pdfBytes = pdfService.generateDevisPdf(devis);

            if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom("contact@kikitraiteursenegal.net", "KIKI TRAITEUR SÉNÉGAL");
                helper.setTo(recipient);
                helper.setSubject("Votre Devis Gastronomique KIKI TRAITEUR SÉNÉGAL (" + devis.getDevisRef() + ")");

                String htmlContent = "<div style='font-family: Arial, sans-serif; color: #1E293B; max-width: 600px; margin: 0 auto; border: 1px solid #E2E8F0; border-radius: 8px; overflow: hidden;'>"
                        + "<div style='background: #7A1C1C; color: white; padding: 20px; text-align: center;'>"
                        + "<h2 style='margin: 0; font-family: Georgia, serif;'>KIKI TRAITEUR SÉNÉGAL</h2>"
                        + "</div>"
                        + "<div style='padding: 24px;'>"
                        + "<p>Bonjour <strong>" + (devis.getClientName() != null ? devis.getClientName() : "") + "</strong>,</p>"
                        + "<p>Nous vous remercions de votre confiance en <strong>KIKI TRAITEUR SÉNÉGAL</strong> pour votre événement.</p>"
                        + "<p>Veuillez trouver ci-joint votre devis personnalisé <strong>" + devis.getDevisRef() + "</strong> au format PDF.</p>"
                        + "<p>Pour toute question ou pour valider cette offre, vous pouvez nous répondre directement à cette adresse e-mail : <a href='mailto:contact@kikitraiteursenegal.net'>contact@kikitraiteursenegal.net</a>.</p>"
                        + "<br/><p>Gastronomiquement vôtre,<br/><strong>L'équipe Kiki Traiteur</strong></p>"
                        + "</div>"
                        + "<div style='background: #F8FAFC; padding: 12px; text-align: center; font-size: 12px; color: #64748B; border-top: 1px solid #E2E8F0;'>"
                        + "contact@kikitraiteursenegal.net | Dakar, Sénégal"
                        + "</div>"
                        + "</div>";

                helper.setText(htmlContent, true);
                helper.addAttachment("Devis_" + devis.getDevisRef() + ".pdf", new ByteArrayResource(pdfBytes));

                mailSender.send(message);
                log.info("Email avec Devis PDF envoyé avec succès à {} depuis contact@kikitraiteursenegal.net", recipient);
            } else {
                log.info("JavaMailSender non initialisé - Simulation de l'envoi email du Devis PDF à {} depuis contact@kikitraiteursenegal.net", recipient);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de l'email Devis PDF à {} (les données du devis ont bien été enregistrées) : {}", recipient, e.getMessage());
        }
    }
}
