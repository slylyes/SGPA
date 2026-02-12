package com.pharmacie.controller;

import com.pharmacie.dao.VenteDAO;
import com.pharmacie.model.LigneVente;
import com.pharmacie.model.Medicament;
import com.pharmacie.model.Vente;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur pour la gestion des ventes
 */
public class VenteController {
    private VenteDAO venteDAO;
    private MedicamentController medicamentController;

    public VenteController() {
        this.venteDAO = new VenteDAO();
        this.medicamentController = new MedicamentController();
    }

    /**
     * Enregistre une nouvelle vente
     */
    public boolean enregistrerVente(Vente vente) {
        // Validation
        if (vente.getLignesVente() == null || vente.getLignesVente().isEmpty()) {
            System.err.println("La vente doit contenir au moins un médicament");
            return false;
        }

        // Vérifier le stock et la péremption pour chaque médicament
        for (LigneVente ligne : vente.getLignesVente()) {
            Medicament medicament = medicamentController.getMedicamentParId(ligne.getIdMedicament());
            if (medicament == null) {
                System.err.println("Médicament introuvable : " + ligne.getNomMedicament());
                return false;
            }
            if (medicament.getDatePeremption().isBefore(java.time.LocalDate.now())) {
                System.err.println("Médicament périmé (date : " + medicament.getDatePeremption() + ") : " + ligne.getNomMedicament());
                return false;
            }
            if (medicament.getStockActuel() < ligne.getQuantite()) {
                System.err.println("Stock insuffisant pour : " + ligne.getNomMedicament());
                return false;
            }
        }

        // Enregistrer la vente
        boolean venteCreee = venteDAO.creer(vente);
        
        if (venteCreee) {
            // Mettre à jour les stocks
            for (LigneVente ligne : vente.getLignesVente()) {
                medicamentController.diminuerStock(ligne.getIdMedicament(), ligne.getQuantite());
            }
            return true;
        }
        
        return false;
    }



    /**
     * Récupère toutes les ventes
     */
    public List<Vente> getToutesVentes() {
        return venteDAO.lireTous();
    }

    /**
     * Récupère une vente par son ID
     */
    public Vente getVenteParId(int id) {
        return venteDAO.lireParId(id);
    }

    /**
     * Récupère les ventes d'une période
     */
    public List<Vente> getVentesParPeriode(LocalDateTime debut, LocalDateTime fin) {
        return venteDAO.lireParPeriode(debut, fin);
    }

    /**
     * Calcule le chiffre d'affaires total
     */
    public double getChiffreAffaires() {
        return venteDAO.getChiffreAffaires();
    }

    /**
     * Calcule le nombre total de ventes
     */
    public int getNombreVentes() {
        return venteDAO.lireTous().size();
    }
}
