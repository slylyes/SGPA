package com.pharmacie.controller;

import com.pharmacie.dao.RapportDAO;
import com.pharmacie.model.RapportVente;
import com.pharmacie.model.TopMedicament;
import com.pharmacie.service.ExcelExporter;
import com.pharmacie.service.PdfExporter;

import java.io.File;
import java.time.LocalDate;
import java.util.List;


public class RapportController {
    private RapportDAO rapportDAO;

    public RapportController() {
        this.rapportDAO = new RapportDAO();
    }

   
    public List<RapportVente> getVentesParMoisEntreDates(LocalDate debut, LocalDate fin) {
        return rapportDAO.getVentesParMoisEntreDates(debut, fin);
    }


    public List<TopMedicament> getTopMedicaments(int limite, LocalDate debut, LocalDate fin) {
        return rapportDAO.getTopMedicaments(limite, debut, fin);
    }

    
    public double getChiffreAffairesPeriode(LocalDate debut, LocalDate fin) {
        return rapportDAO.getChiffreAffairesPeriode(debut, fin);
    }

   
    public int getNombreVentesPeriode(LocalDate debut, LocalDate fin) {
        return rapportDAO.getNombreVentesPeriode(debut, fin);
    }


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
