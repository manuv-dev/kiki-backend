package com.kikitraiteur.api_kikitraiteur.Client.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kikitraiteur.api_kikitraiteur.Client.model.Devis;
import com.kikitraiteur.api_kikitraiteur.Client.model.DevisItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DevisPdfService {

    private static final BaseColor BORDEAUX = new BaseColor(114, 21, 19);
    private static final BaseColor RED = new BaseColor(229, 29, 36);
    private static final BaseColor BEIGE = new BaseColor(230, 218, 186);
    private static final BaseColor DARK_TEXT = new BaseColor(40, 40, 40);
    private static final BaseColor LIGHT_TEXT = new BaseColor(100, 100, 100);
    private static final BaseColor GRAY_BG = new BaseColor(240, 240, 240);
    private static final BaseColor TABLE_BORDER = new BaseColor(220, 220, 220);

    public byte[] generateDevisPdf(Devis devis) throws DocumentException {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        addHeaderAndTitle(document, devis);
        addSeparator(document, BORDEAUX, 1f, 15f);
        addCompanyAndClientInfo(document, devis);
        addAdditionalInfo(document, devis);
        addSeparator(document, BORDEAUX, 1.5f, 10f);
        addItemsTable(document, devis);
        addFinancialSummary(document, devis);
        addSignatureBox(document);
        addSeparator(document, TABLE_BORDER, 1f, 15f);
        addFooter(document);

        document.close();
        return baos.toByteArray();
    }

    private void addHeaderAndTitle(Document document, Devis devis) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 1f});

        String ref = devis.getDevisRef() != null ? devis.getDevisRef() : "123";
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BORDEAUX);
        
        PdfPCell leftCell = new PdfPCell(new Phrase("DEVIS n° " + ref, fontTitle));
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);
        leftCell.setPaddingTop(10f);
        table.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        try {
            ClassPathResource resource = new ClassPathResource("static/KIKI TRAITEUR Logo Rouge bordeaux.png");
            InputStream is = resource.getInputStream();
            Image logo = Image.getInstance(is.readAllBytes());
            logo.scaleToFit(120f, 120f);
            logo.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(logo);
        } catch (Exception e) {
            log.warn("Logo not found");
        }
        
        table.addCell(rightCell);
        document.add(table);
    }

    private void addCompanyAndClientInfo(Document document, Devis devis) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.1f, 0.9f});
        table.setSpacingAfter(20f);

        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA, 10, LIGHT_TEXT);
        Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 10, DARK_TEXT);
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, DARK_TEXT);

        // Left: Info Devis
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1.3f, 1f});
        
        String dateStr = devis.getDateCreated() != null ? formatDate(devis.getDateCreated()) : formatDate(LocalDate.now().toString());
        String valDateStr = formatDate(LocalDate.now().plusDays(2).toString());
        String datePrestation = devis.getDate() != null ? formatDate(devis.getDate()) : "À définir";

        String gestName = devis.getGestionnaireName() != null ? devis.getGestionnaireName() : "Le Gestionnaire Kiki Traiteur";
        addInfoRow(infoTable, "Date du devis :", dateStr, fontLabel, fontValue);
        addInfoRow(infoTable, "Référence du devis :", devis.getDevisRef() != null ? devis.getDevisRef() : "123", fontLabel, fontValue);
        addInfoRow(infoTable, "Date de validité du devis :", valDateStr, fontLabel, fontValue);
        addInfoRow(infoTable, "Émis par :", gestName, fontLabel, fontValue);
        addInfoRow(infoTable, "Contact client :", devis.getClientName() != null ? devis.getClientName() : "Client", fontLabel, fontValue);
        addInfoRow(infoTable, "Date de début de la prestation :", datePrestation, fontLabel, fontValue);

        PdfPCell leftCell = new PdfPCell(infoTable);
        leftCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(leftCell);

        // Right: Destinataire
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPaddingLeft(30f);
        
        rightCell.addElement(new Paragraph("Destinataire", fontTitle));
        rightCell.addElement(new Paragraph(devis.getClientName() != null ? devis.getClientName() : "Client", fontValue));
        rightCell.addElement(new Paragraph(devis.getClientEmail() != null ? devis.getClientEmail() : "", fontValue));
        rightCell.addElement(new Paragraph(devis.getClientPhone() != null ? devis.getClientPhone() : "+221 77 777 77 77", fontValue));

        table.addCell(rightCell);
        document.add(table);
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font fontLabel, Font fontValue) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fontLabel));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPaddingBottom(4f);
        PdfPCell c2 = new PdfPCell(new Phrase(value, fontValue));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPaddingBottom(4f);
        table.addCell(c1);
        table.addCell(c2);
    }

    private void addAdditionalInfo(Document document, Devis devis) throws DocumentException {
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DARK_TEXT);
        Font fontText = FontFactory.getFont(FontFactory.HELVETICA, 10, LIGHT_TEXT);
        
        Paragraph para = new Paragraph();
        para.add(new Chunk("Informations additionnelles\n", fontTitle));
        String location = devis.getLocation() != null ? devis.getLocation() : "Dakar";
        String guests = devis.getGuests() != null ? String.valueOf(devis.getGuests()) : "N/A";
        String prestation = getPrestationLabel(devis.getPrestationId());
        
        para.add(new Chunk("Prestation : " + prestation + " - Lieu : " + location + " - Convives : " + guests, fontText));
        para.setSpacingAfter(10f);
        document.add(para);
    }

    private void addItemsTable(Document document, Devis devis) throws DocumentException {
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font fontRow = FontFactory.getFont(FontFactory.HELVETICA, 10, DARK_TEXT);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 2f});
        table.setSpacingAfter(10f);

        PdfPCell h1 = new PdfPCell(new Phrase("DÉSIGNATION / DESCRIPTION", fontHeader));
        PdfPCell h2 = new PdfPCell(new Phrase("MONTANT (FCFA)", fontHeader));

        h1.setBackgroundColor(BORDEAUX); h1.setBorder(Rectangle.NO_BORDER); h1.setPadding(8f);
        h2.setBackgroundColor(BORDEAUX); h2.setBorder(Rectangle.NO_BORDER); h2.setPadding(8f); h2.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(h1); table.addCell(h2);

        DecimalFormat df = new DecimalFormat("#,###");

        if (devis.getItems() != null && !devis.getItems().isEmpty()) {
            for (DevisItem item : devis.getItems()) {
                double price = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;
                int qty = item.getQty() != null ? item.getQty() : 1;
                double total = price * qty;

                PdfPCell c1 = new PdfPCell(new Phrase(item.getDesc(), fontRow));
                PdfPCell c2 = new PdfPCell(new Phrase(df.format(total) + " FCFA", fontRow));

                c1.setBorder(Rectangle.BOX); c1.setBorderColor(TABLE_BORDER); c1.setPadding(8f);
                c2.setBorder(Rectangle.BOX); c2.setBorderColor(TABLE_BORDER); c2.setPadding(8f); c2.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(c1); table.addCell(c2);
            }
        }
        document.add(table);
    }

    private void addFinancialSummary(Document document, Devis devis) throws DocumentException {
        DecimalFormat df = new DecimalFormat("#,###");
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DARK_TEXT);
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BORDEAUX);

        double subtotal = devis.getItems() == null ? 0.0 : devis.getItems().stream().mapToDouble(i -> (i.getUnitPrice() != null ? i.getUnitPrice() : 0.0) * (i.getQty() != null ? i.getQty() : 1)).sum();
        double discount = devis.getDiscount() != null ? devis.getDiscount() : 0.0;
        double remiseMt = subtotal * (discount / 100.0);
        double totalNet = subtotal - remiseMt;

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setWidths(new float[]{1f, 1.5f});
        table.setSpacingAfter(20f);

        PdfPCell l1 = new PdfPCell(new Phrase("Total HT", fontBold)); l1.setBorder(Rectangle.NO_BORDER); l1.setPadding(5f);
        PdfPCell v1 = new PdfPCell(new Phrase(df.format(subtotal) + " FCFA", fontBold)); v1.setBorder(Rectangle.NO_BORDER); v1.setHorizontalAlignment(Element.ALIGN_RIGHT); v1.setPadding(5f);
        table.addCell(l1); table.addCell(v1);

        if (discount > 0) {
            PdfPCell l2 = new PdfPCell(new Phrase("Remise", fontBold)); l2.setBorder(Rectangle.NO_BORDER); l2.setPadding(5f);
            PdfPCell v2 = new PdfPCell(new Phrase("- " + df.format(remiseMt) + " FCFA", fontBold)); v2.setBorder(Rectangle.NO_BORDER); v2.setHorizontalAlignment(Element.ALIGN_RIGHT); v2.setPadding(5f);
            table.addCell(l2); table.addCell(v2);
        }

        Font fontTotalWhite = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        PdfPCell l3 = new PdfPCell(new Phrase("TOTAL NET", fontTotalWhite)); 
        l3.setBackgroundColor(BORDEAUX); l3.setBorder(Rectangle.NO_BORDER); l3.setPadding(8f);
        
        PdfPCell v3 = new PdfPCell(new Phrase(df.format(totalNet) + " FCFA", fontTotalWhite)); 
        v3.setBackgroundColor(BORDEAUX); v3.setBorder(Rectangle.NO_BORDER); v3.setHorizontalAlignment(Element.ALIGN_RIGHT); v3.setPadding(8f);
        
        table.addCell(l3); table.addCell(v3);

        document.add(table);
    }

    private void addSignatureBox(Document document) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 9, DARK_TEXT);
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell cell = new PdfPCell(new Phrase("Signature du client (précédée de la mention « Bon pour accord »)", font));
        cell.setBackgroundColor(BEIGE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(60f);
        cell.setPadding(10f);
        
        table.addCell(cell);
        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BORDEAUX);
        Font fontText = FontFactory.getFont(FontFactory.HELVETICA, 8, LIGHT_TEXT);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 1f, 1f});
        
        PdfPCell c1 = new PdfPCell(); c1.setBorder(Rectangle.NO_BORDER);
        c1.addElement(new Phrase("KIKI TRAITEUR\n", fontTitle));
        c1.addElement(new Phrase("Dakar, Sénégal\nNINEA : SN-2024-KTS-001", fontText));
        
        PdfPCell c2 = new PdfPCell(); c2.setBorder(Rectangle.NO_BORDER);
        c2.addElement(new Phrase("CONTACT\n", fontTitle));
        c2.addElement(new Phrase("+221 77 000 00 00\ncontact@kikitraiteursenegal.net\nwww.kikitraiteursenegal.net", fontText));
        
        PdfPCell c3 = new PdfPCell(); c3.setBorder(Rectangle.NO_BORDER);
        c3.addElement(new Phrase("RÉSEAUX SOCIAUX\n", fontTitle));
        c3.addElement(new Phrase("Instagram : @kikitraiteursenegal\nFacebook : Kiki Traiteur\nWhatsApp : +221 77 000 00 00", fontText));
        
        table.addCell(c1); table.addCell(c2); table.addCell(c3);
        document.add(table);
    }

    private void addSeparator(Document document, BaseColor color, float thickness, float spaceAfter) throws DocumentException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingAfter(spaceAfter);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(thickness);
        line.addCell(cell);
        document.add(line);
    }

    private String getPrestationLabel(String id) {
        if (id == null) return "Prestation Traiteur";
        return switch (id) {
            case "salle-diva" -> "Location Salle La Diva";
            case "traiteur" -> "Service Traiteur Prestige";
            case "evenements" -> "Organisation d'Événements";
            case "foodtruck" -> "Food Truck Gourmet";
            case "takeaway" -> "Plats à Emporter";
            default -> id;
        };
    }

    private String formatDate(String raw) {
        if (raw == null || raw.isBlank()) return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        try {
            return LocalDate.parse(raw).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return raw;
        }
    }
}
