package com.pharmacie.controller;

import com.pharmacie.dao.RapportDAO;
import com.pharmacie.model.RapportVente;
import com.pharmacie.model.TopMedicament;
import com.pharmacie.service.ExcelExporter;
import com.pharmacie.service.PdfExporter;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur pour la génération de rapports financiers
 */
public class RapportController {
    private RapportDAO rapportDAO;

    public RapportController() {
        this.rapportDAO = new RapportDAO();
    }

    /**
     * Récupère les ventes agrégées par mois pour une année
     */
    public List<RapportVente> getVentesParMois(int annee) {
        return rapportDAO.getVentesParMois(annee);
    }

    /**
     * Récupère les ventes agrégées par mois entre deux dates
     */
    public List<RapportVente> getVentesParMoisEntreDates(LocalDate debut, LocalDate fin) {
        return rapportDAO.getVentesParMoisEntreDates(debut, fin);
    }

    /**
     * Récupère le top des médicaments les plus vendus
     */
    public List<TopMedicament> getTopMedicaments(int limite, LocalDate debut, LocalDate fin) {
        return rapportDAO.getTopMedicaments(limite, debut, fin);
    }

    /**
     * Récupère le chiffre d'affaires total pour une période
     */
    public double getChiffreAffairesPeriode(LocalDate debut, LocalDate fin) {
        return rapportDAO.getChiffreAffairesPeriode(debut, fin);
    }

    /**
     * Récupère le nombre total de ventes pour une période
     */
    public int getNombreVentesPeriode(LocalDate debut, LocalDate fin) {
        return rapportDAO.getNombreVentesPeriode(debut, fin);
    }

    /**
     * Récupère les années pour lesquelles des ventes existent
     */
    public List<Integer> getAnneesDisponibles() {
        return rapportDAO.getAnneesDisponibles();
    }

    /**
     * Exporte le rapport en Excel
     * @return le fichier créé, ou null en cas d'erreur
     */
    public File exporterExcel(List<RapportVente> ventesParMois, List<TopMedicament> topMedicaments,
                              double caTotalPeriode, int nbVentesPeriode,
                              LocalDate debut, LocalDate fin, File fichier) {
        try {
            return ExcelExporter.exporter(ventesParMois, topMedicaments, caTotalPeriode,
                    nbVentesPeriode, debut, fin, fichier);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export Excel : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Exporte le rapport en PDF
     * @return le fichier créé, ou null en cas d'erreur
     */
    public File exporterPdf(List<RapportVente> ventesParMois, List<TopMedicament> topMedicaments,
                            double caTotalPeriode, int nbVentesPeriode,
                            LocalDate debut, LocalDate fin, File fichier) {
        try {
            return PdfExporter.exporter(ventesParMois, topMedicaments, caTotalPeriode,
                    nbVentesPeriode, debut, fin, fichier);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export PDF : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
