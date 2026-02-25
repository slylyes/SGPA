package com.pharmacie.model;


public class LigneVente {
    private int id;
    private int idVente;
    private int idMedicament;
    private String nomMedicament;  // Pour affichage
    private int quantite;
    private double prixUnitaire;
    private double sousTotal;

    // Constructeur complet
    public LigneVente(int id, int idVente, int idMedicament, String nomMedicament, 
                     int quantite, double prixUnitaire) {
        this.id = id;
        this.idVente = idVente;
        this.idMedicament = idMedicament;
        this.nomMedicament = nomMedicament;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = quantite * prixUnitaire;
    }

    // Constructeur simplifié
    public LigneVente(int idMedicament, String nomMedicament, int quantite, double prixUnitaire) {
        this.idMedicament = idMedicament;
        this.nomMedicament = nomMedicament;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = quantite * prixUnitaire;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
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
        this.sousTotal = quantite * prixUnitaire;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = quantite * prixUnitaire;
    }

    public double getSousTotal() {
        return sousTotal;
    }

    @Override
    public String toString() {
        return nomMedicament + " x" + quantite + " = " + sousTotal + "€";
    }
}
