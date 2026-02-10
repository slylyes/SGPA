package com.pharmacie.controller;

import com.pharmacie.dao.MedicamentDAO;
import com.pharmacie.model.Medicament;
import java.util.List;

/**
 * Contrôleur pour la gestion des médicaments
 */
public class MedicamentController {
    private MedicamentDAO medicamentDAO;

    public MedicamentController() {
        this.medicamentDAO = new MedicamentDAO();
    }

    /**
     * Ajoute un nouveau médicament
     */
    public boolean ajouterMedicament(Medicament medicament) {
        // Validation
        if (medicament.getNomCommercial() == null || medicament.getNomCommercial().trim().isEmpty()) {
            System.err.println("Le nom commercial est obligatoire");
            return false;
        }
        if (medicament.getPrincipeActif() == null || medicament.getPrincipeActif().trim().isEmpty()) {
            System.err.println("Le principe actif est obligatoire");
            return false;
        }
        if (medicament.getPrixPublic() <= 0) {
            System.err.println("Le prix doit être strictement positif");
            return false;
        }
        if (medicament.getStockActuel() < 0) {
            System.err.println("Le stock ne peut pas être négatif");
            return false;
        }
        if (medicament.getSeuilMinimum() < 0) {
            System.err.println("Le seuil minimum ne peut pas être négatif");
            return false;
        }
        if (medicament.getDatePeremption() == null) {
            System.err.println("La date de péremption est obligatoire");
            return false;
        }
        if (medicament.getDatePeremption().isBefore(java.time.LocalDate.now())) {
            System.err.println("La date de péremption ne peut pas être dans le passé");
            return false;
        }
        
        return medicamentDAO.creer(medicament);
    }

    /**
     * Modifie un médicament existant
     */
    public boolean modifierMedicament(Medicament medicament) {
        if (medicament.getId() <= 0) {
            System.err.println("ID invalide");
            return false;
        }
        if (medicament.getPrixPublic() <= 0) {
            System.err.println("Le prix doit être strictement positif");
            return false;
        }
        if (medicament.getStockActuel() < 0) {
            System.err.println("Le stock ne peut pas être négatif");
            return false;
        }
        if (medicament.getDatePeremption().isBefore(java.time.LocalDate.now())) {
            System.err.println("La date de péremption ne peut pas être dans le passé");
            return false;
        }
        return medicamentDAO.mettreAJour(medicament);
    }

    /**
     * Archive un médicament (suppression logique)
     */
    public boolean supprimerMedicament(int id) {
        return medicamentDAO.supprimer(id);
    }
    
    /**
     * Réactive un médicament archivé
     */
    public boolean reactiverMedicament(int id) {
        return medicamentDAO.reactiver(id);
    }
    
    /**
     * Récupère tous les médicaments archivés
     */
    public List<Medicament> getMedicamentsArchives() {
        return medicamentDAO.lireTousArchives();
    }

    /**
     * Récupère tous les médicaments actifs
     */
    public List<Medicament> getTousMedicaments() {
        return medicamentDAO.lireTous();
    }

    /**
     * Recherche des médicaments actifs par nom
     */
    public List<Medicament> rechercherMedicaments(String nom) {
        return medicamentDAO.rechercherParNom(nom);
    }

    /**
     * Récupère un médicament par son ID
     */
    public Medicament getMedicamentParId(int id) {
        if (id <= 0) {
            System.err.println("ID invalide");
            return null;
        }
        return medicamentDAO.lireParId(id);
    }

    /**
     * Récupère les médicaments en alerte de stock
     */
    public List<Medicament> getMedicamentsEnAlerteStock() {
        return medicamentDAO.getMedicamentsEnAlerteStock();
    }

    /**
     * Récupère les médicaments proches de la péremption
     */
    public List<Medicament> getMedicamentsProchesPeremption() {
        return medicamentDAO.getMedicamentsProchesPeremption();
    }

    /**
     * Met à jour le stock d'un médicament
     */
    public boolean mettreAJourStock(int idMedicament, int nouvelleQuantite) {
        if (nouvelleQuantite < 0) {
            System.err.println("La quantité ne peut pas être négative");
            return false;
        }
        return medicamentDAO.mettreAJourStock(idMedicament, nouvelleQuantite);
    }

    /**
     * Diminue le stock après une vente
     */
    public boolean diminuerStock(int idMedicament, int quantite) {
        if (quantite <= 0) {
            System.err.println("La quantité doit être strictement positive");
            return false;
        }
        
        Medicament medicament = medicamentDAO.lireParId(idMedicament);
        if (medicament == null) {
            System.err.println("Médicament introuvable");
            return false;
        }
        
        int nouveauStock = medicament.getStockActuel() - quantite;
        if (nouveauStock < 0) {
            System.err.println("Stock insuffisant : impossible de diminuer le stock");
            return false;
        }
        
        return medicamentDAO.mettreAJourStock(idMedicament, nouveauStock);
    }

    /**
     * Augmente le stock après une réception
     */
    public boolean augmenterStock(int idMedicament, int quantite) {
        if (quantite <= 0) {
            System.err.println("La quantité doit être strictement positive");
            return false;
        }
        
        Medicament medicament = medicamentDAO.lireParId(idMedicament);
        if (medicament == null) {
            System.err.println("Médicament introuvable");
            return false;
        }
        
        int nouveauStock = medicament.getStockActuel() + quantite;
        return medicamentDAO.mettreAJourStock(idMedicament, nouveauStock);
    }
}
