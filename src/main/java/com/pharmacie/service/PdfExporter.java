package com.pharmacie.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pharmacie.model.RapportVente;
import com.pharmacie.model.TopMedicament;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service d'export des rapports financiers au format PDF
 */
public class PdfExporter {

    // Couleurs du thème pharmacie
    private static final Color VERT_FONCE = new Color(46, 125, 50);    // #2E7D32
    private static final Color VERT_CLAIR = new Color(200, 230, 201);  // #C8E6C9
    private static final Color GRIS_CLAIR = new Color(245, 245, 245);

    // Polices
    private static final Font FONT_TITRE = new Font(Font.HELVETICA, 20, Font.BOLD, VERT_FONCE);
    private static final Font FONT_SOUS_TITRE = new Font(Font.HELVETICA, 14, Font.BOLD, VERT_FONCE);
    private static final Font FONT_HEADER = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
    private static final Font FONT_TOTAL = new Font(Font.HELVETICA, 11, Font.BOLD, VERT_FONCE);
    private static final Font FONT_INFO = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);

    /**
     * Exporte un rapport complet en PDF
     */
    public static File exporter(List<RapportVente> ventesParMois, List<TopMedicament> topMedicaments,
                                double caTotalPeriode, int nbVentesPeriode,
                                LocalDate debut, LocalDate fin, File fichier) throws IOException, DocumentException {

        Document document = new Document(PageSize.A4, 40, 40, 50, 40);
        PdfWriter.getInstance(document, new FileOutputStream(fichier));
        document.open();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // === EN-TÊTE ===
        Paragraph titre = new Paragraph("Rapport Financier", FONT_TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        document.add(titre);

        Paragraph sousTitre = new Paragraph("Pharmacie Dauphine - SGPA", FONT_SOUS_TITRE);
        sousTitre.setAlignment(Element.ALIGN_CENTER);
        sousTitre.setSpacingAfter(5);
        document.add(sousTitre);

        // Ligne séparatrice
        document.add(new Paragraph("─".repeat(80), new Font(Font.HELVETICA, 6, Font.NORMAL, VERT_CLAIR)));

        // Infos période
        Paragraph infos = new Paragraph();
        infos.setSpacingBefore(10);
        infos.setSpacingAfter(15);
        infos.add(new Chunk("Période : ", FONT_BOLD));
        infos.add(new Chunk(debut.format(fmt) + " → " + fin.format(fmt) + "\n", FONT_NORMAL));
        infos.add(new Chunk("Généré le : ", FONT_BOLD));
        infos.add(new Chunk(LocalDate.now().format(fmt), FONT_NORMAL));
        document.add(infos);

        // === INDICATEURS CLÉS ===
        ajouterIndicateurs(document, caTotalPeriode, nbVentesPeriode);

        document.add(Chunk.NEWLINE);

        // === VENTES PAR MOIS ===
        ajouterTableauVentes(document, ventesParMois);

        document.add(Chunk.NEWLINE);

        // === TOP MÉDICAMENTS ===
        ajouterTableauTopMedicaments(document, topMedicaments);

        // === PIED DE PAGE ===
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph("Document généré automatiquement par SGPA - Système de Gestion Pharmacie Avancé", FONT_INFO);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return fichier;
    }

    private static void ajouterIndicateurs(Document document, double caTotalPeriode, int nbVentesPeriode)
            throws DocumentException {

        document.add(new Paragraph("Indicateurs clés", FONT_SOUS_TITRE));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // CA total
        ajouterIndicateurCell(table, "Chiffre d'affaires", String.format("%.2f €", caTotalPeriode));

        // Nb ventes
        ajouterIndicateurCell(table, "Nombre de ventes", String.valueOf(nbVentesPeriode));

        // Panier moyen
        double panierMoyen = nbVentesPeriode > 0 ? caTotalPeriode / nbVentesPeriode : 0;
        ajouterIndicateurCell(table, "Panier moyen", String.format("%.2f €", panierMoyen));

        document.add(table);
    }

    private static void ajouterIndicateurCell(PdfPTable table, String label, String valeur) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(VERT_CLAIR);
        cell.setBorderWidth(1.5f);
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(GRIS_CLAIR);

        Paragraph pLabel = new Paragraph(label, FONT_BOLD);
        pLabel.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pLabel);

        Paragraph pValeur = new Paragraph(valeur, FONT_TOTAL);
        pValeur.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(pValeur);

        table.addCell(cell);
    }

    private static void ajouterTableauVentes(Document document, List<RapportVente> ventesParMois)
            throws DocumentException {

        document.add(new Paragraph("Ventes par mois", FONT_SOUS_TITRE));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{3, 2, 2, 3});

        // En-têtes
        ajouterCellHeader(table, "Période");
        ajouterCellHeader(table, "Nb Ventes");
        ajouterCellHeader(table, "Qté vendue");
        ajouterCellHeader(table, "CA (€)");

        // Données
        double totalCA = 0;
        int totalVentes = 0;
        int totalQte = 0;
        boolean ligneAlternee = false;

        for (RapportVente rv : ventesParMois) {
            Color bgColor = ligneAlternee ? GRIS_CLAIR : Color.WHITE;

            ajouterCellNormale(table, rv.getPeriode(), bgColor);
            ajouterCellNormale(table, String.valueOf(rv.getNombreVentes()), bgColor);
            ajouterCellNormale(table, String.valueOf(rv.getQuantiteTotale()), bgColor);
            ajouterCellMontant(table, rv.getChiffreAffaires(), bgColor);

            totalCA += rv.getChiffreAffaires();
            totalVentes += rv.getNombreVentes();
            totalQte += rv.getQuantiteTotale();
            ligneAlternee = !ligneAlternee;
        }

        // Ligne TOTAL
        ajouterCellTotal(table, "TOTAL");
        ajouterCellTotal(table, String.valueOf(totalVentes));
        ajouterCellTotal(table, String.valueOf(totalQte));
        ajouterCellTotal(table, String.format("%.2f €", totalCA));

        document.add(table);
    }

    private static void ajouterTableauTopMedicaments(Document document, List<TopMedicament> topMedicaments)
            throws DocumentException {

        document.add(new Paragraph("Top médicaments les plus vendus", FONT_SOUS_TITRE));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1, 4, 2, 2, 3});

        // En-têtes
        ajouterCellHeader(table, "#");
        ajouterCellHeader(table, "Médicament");
        ajouterCellHeader(table, "Qté vendue");
        ajouterCellHeader(table, "Nb Ventes");
        ajouterCellHeader(table, "CA (€)");

        // Données
        int rang = 1;
        boolean ligneAlternee = false;
        for (TopMedicament tm : topMedicaments) {
            Color bgColor = ligneAlternee ? GRIS_CLAIR : Color.WHITE;

            ajouterCellNormale(table, String.valueOf(rang++), bgColor);
            ajouterCellNormale(table, tm.getNomCommercial(), bgColor);
            ajouterCellNormale(table, String.valueOf(tm.getQuantiteTotaleVendue()), bgColor);
            ajouterCellNormale(table, String.valueOf(tm.getNombreVentes()), bgColor);
            ajouterCellMontant(table, tm.getChiffreAffaires(), bgColor);

            ligneAlternee = !ligneAlternee;
        }

        document.add(table);
    }

    // === Cellules utilitaires ===

    private static void ajouterCellHeader(PdfPTable table, String texte) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, FONT_HEADER));
        cell.setBackgroundColor(VERT_FONCE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private static void ajouterCellNormale(PdfPTable table, String texte, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, FONT_NORMAL));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        cell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private static void ajouterCellMontant(PdfPTable table, double montant, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(String.format("%.2f €", montant), FONT_BOLD));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(6);
        cell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private static void ajouterCellTotal(PdfPTable table, String texte) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, FONT_TOTAL));
        cell.setBackgroundColor(VERT_CLAIR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorderWidth(1.5f);
        cell.setBorderColor(VERT_FONCE);
        table.addCell(cell);
    }
}
