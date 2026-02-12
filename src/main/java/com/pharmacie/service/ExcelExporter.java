package com.pharmacie.service;

import com.pharmacie.model.RapportVente;
import com.pharmacie.model.TopMedicament;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service d'export des rapports financiers au format Excel (.xlsx)
 */
public class ExcelExporter {

    /**
     * Exporte un rapport complet en Excel
     */
    public static File exporter(List<RapportVente> ventesParMois, List<TopMedicament> topMedicaments,
                                double caTotalPeriode, int nbVentesPeriode,
                                LocalDate debut, LocalDate fin, File fichier) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            // === Styles ===
            CellStyle styleTitre = createTitreStyle(workbook);
            CellStyle styleHeader = createHeaderStyle(workbook);
            CellStyle styleNormal = createNormalStyle(workbook);
            CellStyle styleMontant = createMontantStyle(workbook);
            CellStyle styleTotalLabel = createTotalLabelStyle(workbook);
            CellStyle styleTotalValeur = createTotalValeurStyle(workbook);

            // === Feuille 1 : Résumé ===
            Sheet sheetResume = workbook.createSheet("Résumé");
            creerFeuilleResume(sheetResume, caTotalPeriode, nbVentesPeriode, debut, fin,
                              styleTitre, styleHeader, styleNormal, styleMontant);

            // === Feuille 2 : Ventes par mois ===
            Sheet sheetVentes = workbook.createSheet("Ventes par mois");
            creerFeuilleVentesParMois(sheetVentes, ventesParMois, styleTitre, styleHeader,
                                      styleNormal, styleMontant, styleTotalLabel, styleTotalValeur);

            // === Feuille 3 : Top médicaments ===
            Sheet sheetTop = workbook.createSheet("Top médicaments");
            creerFeuilleTopMedicaments(sheetTop, topMedicaments, styleTitre, styleHeader,
                                       styleNormal, styleMontant);

            // Écrire le fichier
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                workbook.write(fos);
            }
        }
        return fichier;
    }

    private static void creerFeuilleResume(Sheet sheet, double caTotalPeriode, int nbVentesPeriode,
                                            LocalDate debut, LocalDate fin,
                                            CellStyle styleTitre, CellStyle styleHeader,
                                            CellStyle styleNormal, CellStyle styleMontant) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int rowIdx = 0;

        // Titre
        Row rowTitre = sheet.createRow(rowIdx++);
        Cell cellTitre = rowTitre.createCell(0);
        cellTitre.setCellValue("Rapport Financier - Pharmacie Dauphine");
        cellTitre.setCellStyle(styleTitre);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        rowIdx++; // Ligne vide

        // Période
        Row rowPeriode = sheet.createRow(rowIdx++);
        createCell(rowPeriode, 0, "Période :", styleHeader);
        createCell(rowPeriode, 1, debut.format(fmt) + " → " + fin.format(fmt), styleNormal);

        // Date de génération
        Row rowDate = sheet.createRow(rowIdx++);
        createCell(rowDate, 0, "Généré le :", styleHeader);
        createCell(rowDate, 1, LocalDate.now().format(fmt), styleNormal);

        rowIdx++; // Ligne vide

        // Indicateurs clés
        Row rowCA = sheet.createRow(rowIdx++);
        createCell(rowCA, 0, "Chiffre d'affaires total :", styleHeader);
        Cell cellCA = rowCA.createCell(1);
        cellCA.setCellValue(caTotalPeriode);
        cellCA.setCellStyle(styleMontant);

        Row rowNb = sheet.createRow(rowIdx++);
        createCell(rowNb, 0, "Nombre de ventes :", styleHeader);
        createCell(rowNb, 1, String.valueOf(nbVentesPeriode), styleNormal);

        Row rowMoy = sheet.createRow(rowIdx++);
        createCell(rowMoy, 0, "Panier moyen :", styleHeader);
        Cell cellMoy = rowMoy.createCell(1);
        cellMoy.setCellValue(nbVentesPeriode > 0 ? caTotalPeriode / nbVentesPeriode : 0);
        cellMoy.setCellStyle(styleMontant);

        // Auto-size
        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);
    }

    private static void creerFeuilleVentesParMois(Sheet sheet, List<RapportVente> ventesParMois,
                                                   CellStyle styleTitre, CellStyle styleHeader,
                                                   CellStyle styleNormal, CellStyle styleMontant,
                                                   CellStyle styleTotalLabel, CellStyle styleTotalValeur) {
        int rowIdx = 0;

        // Titre
        Row rowTitre = sheet.createRow(rowIdx++);
        Cell cellTitre = rowTitre.createCell(0);
        cellTitre.setCellValue("Ventes par mois");
        cellTitre.setCellStyle(styleTitre);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        rowIdx++; // Ligne vide

        // En-têtes
        Row rowHeader = sheet.createRow(rowIdx++);
        String[] headers = {"Période", "Nb Ventes", "Quantité vendue", "Chiffre d'affaires (€)"};
        for (int i = 0; i < headers.length; i++) {
            createCell(rowHeader, i, headers[i], styleHeader);
        }

        // Données
        double totalCA = 0;
        int totalVentes = 0;
        int totalQte = 0;

        for (RapportVente rv : ventesParMois) {
            Row row = sheet.createRow(rowIdx++);
            createCell(row, 0, rv.getPeriode(), styleNormal);
            createCell(row, 1, String.valueOf(rv.getNombreVentes()), styleNormal);
            createCell(row, 2, String.valueOf(rv.getQuantiteTotale()), styleNormal);
            Cell cellCA = row.createCell(3);
            cellCA.setCellValue(rv.getChiffreAffaires());
            cellCA.setCellStyle(styleMontant);

            totalCA += rv.getChiffreAffaires();
            totalVentes += rv.getNombreVentes();
            totalQte += rv.getQuantiteTotale();
        }

        // Ligne total
        rowIdx++; // Ligne vide
        Row rowTotal = sheet.createRow(rowIdx);
        createCell(rowTotal, 0, "TOTAL", styleTotalLabel);
        createCell(rowTotal, 1, String.valueOf(totalVentes), styleTotalLabel);
        createCell(rowTotal, 2, String.valueOf(totalQte), styleTotalLabel);
        Cell cellTotalCA = rowTotal.createCell(3);
        cellTotalCA.setCellValue(totalCA);
        cellTotalCA.setCellStyle(styleTotalValeur);

        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);
    }

    private static void creerFeuilleTopMedicaments(Sheet sheet, List<TopMedicament> topMedicaments,
                                                    CellStyle styleTitre, CellStyle styleHeader,
                                                    CellStyle styleNormal, CellStyle styleMontant) {
        int rowIdx = 0;

        // Titre
        Row rowTitre = sheet.createRow(rowIdx++);
        Cell cellTitre = rowTitre.createCell(0);
        cellTitre.setCellValue("Top médicaments les plus vendus");
        cellTitre.setCellStyle(styleTitre);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        rowIdx++;

        // En-têtes
        Row rowHeader = sheet.createRow(rowIdx++);
        String[] headers = {"#", "Médicament", "Quantité vendue", "Nb Ventes", "CA (€)"};
        for (int i = 0; i < headers.length; i++) {
            createCell(rowHeader, i, headers[i], styleHeader);
        }

        // Données
        int rang = 1;
        for (TopMedicament tm : topMedicaments) {
            Row row = sheet.createRow(rowIdx++);
            createCell(row, 0, String.valueOf(rang++), styleNormal);
            createCell(row, 1, tm.getNomCommercial(), styleNormal);
            createCell(row, 2, String.valueOf(tm.getQuantiteTotaleVendue()), styleNormal);
            createCell(row, 3, String.valueOf(tm.getNombreVentes()), styleNormal);
            Cell cellCA = row.createCell(4);
            cellCA.setCellValue(tm.getChiffreAffaires());
            cellCA.setCellStyle(styleMontant);
        }

        for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);
    }

    // === Méthodes utilitaires de style ===

    private static void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static CellStyle createTitreStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFont(font);
        return style;
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createNormalStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createMontantStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00 €"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private static CellStyle createTotalLabelStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createTotalValeurStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00 €"));
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
