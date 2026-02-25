package com.pharmacie.controller;

import com.pharmacie.dao.MedicamentDAO;
import com.pharmacie.model.Medicament;
import java.util.List;


public class MedicamentController {
    private MedicamentDAO medicamentDAO;

    public MedicamentController() {
        this.medicamentDAO = new MedicamentDAO();
    }

    
    public boolean ajouterMedicament(Medicament medicament) {
        
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

    
    public boolean supprimerMedicament(int id) {
        return medicamentDAO.supprimer(id);
    }

    
    public List<Medicament> getTousMedicaments() {
        return medicamentDAO.lireTous();
    }

    
    public List<Medicament> rechercherMedicaments(String nom) {
        return medicamentDAO.rechercherParNom(nom);
    }

   
    public Medicament getMedicamentParId(int id) {
        if (id <= 0) {
            System.err.println("ID invalide");
            return null;
        }
        return medicamentDAO.lireParId(id);
    }

    
    public List<Medicament> getMedicamentsEnAlerteStock() {
        return medicamentDAO.getMedicamentsEnAlerteStock();
    }

   
    public List<Medicament> getMedicamentsProchesPeremption() {
        return medicamentDAO.getMedicamentsProchesPeremption();
    }

    
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
