package com.pharmacie.model;

/**
 * Classe représentant une ligne de commande
 */
public class LigneCommande {
    private int id;
    private int idCommande;
    private int idMedicament;
    private String nomMedicament;  // Pour affichage
    private int quantite;

    // Constructeur complet
    public LigneCommande(int id, int idCommande, int idMedicament, 
                        String nomMedicament, int quantite) {
        this.id = id;
        this.idCommande = idCommande;
        this.idMedicament = idMedicament;
        this.nomMedicament = nomMedicament;
        this.quantite = quantite;
    }

    // Constructeur simplifié
    public LigneCommande(int idMedicament, String nomMedicament, int quantite) {
        this.idMedicament = idMedicament;
        this.nomMedicament = nomMedicament;
        this.quantite = quantite;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public String getNomMedicament() {
        return nomMedicament;
    }

    public void setNomMedicament(String nomMedicament) {
        this.nomMedicament = nomMedicament;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return nomMedicament + " x" + quantite;
    }
}
