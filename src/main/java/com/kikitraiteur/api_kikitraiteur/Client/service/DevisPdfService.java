package com.kikitraiteur.api_kikitraiteur.Client.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.kikitraiteur.api_kikitraiteur.Client.model.Devis;
import com.kikitraiteur.api_kikitraiteur.Client.model.DevisItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

@Service
public class DevisPdfService {

    public byte[] generateDevisPdf(Devis devis) throws DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Couleurs Premium Kiki Traiteur
        BaseColor bordeaux = new BaseColor(122, 28, 28);
        BaseColor darkSlate = new BaseColor(30, 41, 59);
        BaseColor lightGray = new BaseColor(241, 245, 249);
        BaseColor borderGray = new BaseColor(226, 232, 240);

        // Polices
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, bordeaux);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, darkSlate);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, darkSlate);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, darkSlate);
        Font fontWhiteBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

        // En-tête
        Paragraph header = new Paragraph("KIKI TRAITEUR SÉNÉGAL", fontTitle);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph subheader = new Paragraph("Gastronomie & Événementiel d'Exception\ncontact@kikitraiteursenegal.net | Dakar, Sénégal", fontNormal);
        subheader.setAlignment(Element.ALIGN_CENTER);
        subheader.setSpacingAfter(20f);
        document.add(subheader);

        // Infos Devis & Client
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);

        PdfPCell clientCell = new PdfPCell();
        clientCell.setBorder(Rectangle.BOX);
        clientCell.setBorderColor(borderGray);
        clientCell.setBackgroundColor(lightGray);
        clientCell.setPadding(10f);
        clientCell.addElement(new Paragraph("CLIENT", fontSubtitle));
        clientCell.addElement(new Paragraph("Nom : " + (devis.getClientName() != null ? devis.getClientName() : "Client"), fontNormal));
        clientCell.addElement(new Paragraph("Email : " + (devis.getClientEmail() != null ? devis.getClientEmail() : ""), fontNormal));
        infoTable.addCell(clientCell);

        PdfPCell devisCell = new PdfPCell();
        devisCell.setBorder(Rectangle.BOX);
        devisCell.setBorderColor(borderGray);
        devisCell.setBackgroundColor(lightGray);
        devisCell.setPadding(10f);
        devisCell.addElement(new Paragraph("RÉFÉRENCE DEVIS", fontSubtitle));
        devisCell.addElement(new Paragraph("Réf : " + (devis.getDevisRef() != null ? devis.getDevisRef() : "#DEV-" + devis.getId()), fontNormal));
        devisCell.addElement(new Paragraph("Date : " + (devis.getDateCreated() != null ? devis.getDateCreated() : ""), fontNormal));
        devisCell.addElement(new Paragraph("Prestation : " + (devis.getPrestationId() != null ? devis.getPrestationId() : "Traiteur"), fontNormal));
        infoTable.addCell(devisCell);

        document.add(infoTable);

        // Tableau des prestations
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1f});
        table.setSpacingAfter(15f);

        PdfPCell cellHeaderDesc = new PdfPCell(new Phrase("TITRE / DESCRIPTION DU MONTANT", fontWhiteBold));
        cellHeaderDesc.setBackgroundColor(bordeaux);
        cellHeaderDesc.setPadding(8f);
        table.addCell(cellHeaderDesc);

        PdfPCell cellHeaderMontant = new PdfPCell(new Phrase("MONTANT (FCFA)", fontWhiteBold));
        cellHeaderMontant.setBackgroundColor(bordeaux);
        cellHeaderMontant.setPadding(8f);
        cellHeaderMontant.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellHeaderMontant);

        DecimalFormat df = new DecimalFormat("#,###");
        double subtotal = 0.0;

        if (devis.getItems() != null && !devis.getItems().isEmpty()) {
            for (DevisItem item : devis.getItems()) {
                PdfPCell cellDesc = new PdfPCell(new Phrase(item.getDesc() != null ? item.getDesc() : "", fontNormal));
                cellDesc.setPadding(8f);
                cellDesc.setBorderColor(borderGray);
                table.addCell(cellDesc);

                double price = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;
                subtotal += price;
                PdfPCell cellMontant = new PdfPCell(new Phrase(df.format(price) + " FCFA", fontNormal));
                cellMontant.setPadding(8f);
                cellMontant.setBorderColor(borderGray);
                cellMontant.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellMontant);
            }
        } else {
            PdfPCell cellEmpty = new PdfPCell(new Phrase("Prestation traiteur sur mesure", fontNormal));
            cellEmpty.setPadding(8f);
            table.addCell(cellEmpty);
            table.addCell(new PdfPCell(new Phrase("0 FCFA", fontNormal)));
        }

        document.add(table);

        // Sous-total et Total Net
        double disc = devis.getDiscount() != null ? devis.getDiscount() : 0.0;
        double totalNet = subtotal * (1.0 - (disc / 100.0));

        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(50);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setWidths(new float[]{1.5f, 1f});

        PdfPCell subLabel = new PdfPCell(new Phrase("Sous-total :", fontBold));
        subLabel.setBorder(Rectangle.NO_BORDER);
        subLabel.setPadding(4f);
        totalTable.addCell(subLabel);

        PdfPCell subVal = new PdfPCell(new Phrase(df.format(subtotal) + " FCFA", fontNormal));
        subVal.setBorder(Rectangle.NO_BORDER);
        subVal.setPadding(4f);
        subVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(subVal);

        PdfPCell discLabel = new PdfPCell(new Phrase("Remise (%) :", fontBold));
        discLabel.setBorder(Rectangle.NO_BORDER);
        discLabel.setPadding(4f);
        totalTable.addCell(discLabel);

        PdfPCell discVal = new PdfPCell(new Phrase(df.format(disc) + " %", fontNormal));
        discVal.setBorder(Rectangle.NO_BORDER);
        discVal.setPadding(4f);
        discVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(discVal);

        PdfPCell totLabel = new PdfPCell(new Phrase("TOTAL NET :", fontTitle));
        totLabel.setBorder(Rectangle.TOP);
        totLabel.setBorderColor(bordeaux);
        totLabel.setPaddingTop(8f);
        totalTable.addCell(totLabel);

        Font fontTotalVal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, bordeaux);
        PdfPCell totVal = new PdfPCell(new Phrase(df.format(totalNet) + " FCFA", fontTotalVal));
        totVal.setBorder(Rectangle.TOP);
        totVal.setBorderColor(bordeaux);
        totVal.setPaddingTop(8f);
        totVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(totVal);

        document.add(totalTable);

        // Footer Kiki Traiteur
        Paragraph footer = new Paragraph("\n\nMerci pour votre confiance. Ce devis est valable 30 jours.\nContact : contact@kikitraiteursenegal.net - Tél : +221 77 000 00 00", fontNormal);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30f);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }
}
