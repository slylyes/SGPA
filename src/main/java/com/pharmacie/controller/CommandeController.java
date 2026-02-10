package com.pharmacie.controller;

import com.pharmacie.dao.CommandeDAO;
import com.pharmacie.dao.FournisseurDAO;
import com.pharmacie.model.Commande;
import com.pharmacie.model.Fournisseur;
import com.pharmacie.model.LigneCommande;
import com.pharmacie.model.Medicament;
import java.util.List;

/**
 * Contrôleur pour la gestion des commandes et fournisseurs
 */
public class CommandeController {
    private CommandeDAO commandeDAO;
    private FournisseurDAO fournisseurDAO;
    private MedicamentController medicamentController;

    public CommandeController() {
        this.commandeDAO = new CommandeDAO();
        this.fournisseurDAO = new FournisseurDAO();
        this.medicamentController = new MedicamentController();
    }

    // === Gestion des Fournisseurs ===

    /**
     * Ajoute un nouveau fournisseur
     */
    public boolean ajouterFournisseur(Fournisseur fournisseur) {
        if (fournisseur.getNom() == null || fournisseur.getNom().trim().isEmpty()) {
            System.err.println("Le nom du fournisseur est obligatoire");
            return false;
        }
        return fournisseurDAO.creer(fournisseur);
    }

    /**
     * Modifie un fournisseur
     */
    public boolean modifierFournisseur(Fournisseur fournisseur) {
        return fournisseurDAO.mettreAJour(fournisseur);
    }

    /**
     * Supprime un fournisseur
     */
    public boolean supprimerFournisseur(int id) {
        return fournisseurDAO.supprimer(id);
    }

    /**
     * Récupère tous les fournisseurs
     */
    public List<Fournisseur> getTousFournisseurs() {
        return fournisseurDAO.lireTous();
    }

    /**
     * Récupère un fournisseur par son ID
     */
    public Fournisseur getFournisseurParId(int id) {
        return fournisseurDAO.lireParId(id);
    }

    // === Gestion des Commandes ===

    /**
     * Crée une nouvelle commande
     */
    public boolean creerCommande(Commande commande) {
        if (commande.getLignesCommande() == null || commande.getLignesCommande().isEmpty()) {
            System.err.println("La commande doit contenir au moins un médicament");
            return false;
        }
        return commandeDAO.creer(commande);
    }

    /**
     * Récupère toutes les commandes
     */
    public List<Commande> getToutesCommandes() {
        return commandeDAO.lireTous();
    }

    /**
     * Récupère une commande par son ID
     */
    public Commande getCommandeParId(int id) {
        return commandeDAO.lireParId(id);
    }

    /**
     * Récupère les commandes en attente
     */
    public List<Commande> getCommandesEnAttente() {
        return commandeDAO.getCommandesEnAttente();
    }

    /**
     * Marque une commande comme reçue et met à jour les stocks
     */
    public boolean recevoirCommande(int idCommande) {
        Commande commande = commandeDAO.lireParId(idCommande);
        if (commande == null) {
            System.err.println("Commande introuvable");
            return false;
        }

        // Mettre à jour le statut de la commande
        boolean statutMisAJour = commandeDAO.marquerCommeRecue(idCommande);
        
        if (statutMisAJour) {
            // Mettre à jour les stocks
            for (LigneCommande ligne : commande.getLignesCommande()) {
                medicamentController.augmenterStock(ligne.getIdMedicament(), ligne.getQuantite());
            }
            return true;
        }
        
        return false;
    }

    /**
     * Crée automatiquement des commandes pour les médicaments en alerte
     */
    public int creerCommandesAutomatiques(int idFournisseur) {
        List<Medicament> medicamentsEnAlerte = medicamentController.getMedicamentsEnAlerteStock();
        
        if (medicamentsEnAlerte.isEmpty()) {
            System.out.println("Aucun médicament en alerte de stock");
            return 0;
        }

        Fournisseur fournisseur = fournisseurDAO.lireParId(idFournisseur);
        if (fournisseur == null) {
            System.err.println("Fournisseur introuvable");
            return 0;
        }

        Commande commande = new Commande(idFournisseur, fournisseur.getNom(), 
                                         java.time.LocalDate.now());

        // Ajouter les médicaments en alerte à la commande
        for (Medicament medicament : medicamentsEnAlerte) {
            int quantiteACommander = (medicament.getSeuilMinimum() * 2) - medicament.getStockActuel();
            commande.ajouterLigne(new LigneCommande(
                medicament.getId(),
                medicament.getNomCommercial(),
                quantiteACommander
            ));
        }

        if (creerCommande(commande)) {
            return medicamentsEnAlerte.size();
        }
        
        return 0;
    }
}
