package com.pharmacie.model;

import java.time.LocalDate;


public class Medicament {
    private int id;
    private String nomCommercial;
    private String principeActif;
    private String formeGalenique;  
    private String dosage;
    private double prixPublic;
    private boolean necessiteOrdonnance;
    private LocalDate datePeremption;
    private int stockActuel;
    private int seuilMinimum;
    private boolean actif;

    // Constructeur complet
    public Medicament(int id, String nomCommercial, String principeActif, String formeGalenique,
                     String dosage, double prixPublic, boolean necessiteOrdonnance,
                     LocalDate datePeremption, int stockActuel, int seuilMinimum, boolean actif) {
        this.id = id;
        this.nomCommercial = nomCommercial;
        this.principeActif = principeActif;
        this.formeGalenique = formeGalenique;
        this.dosage = dosage;
        this.prixPublic = prixPublic;
        this.necessiteOrdonnance = necessiteOrdonnance;
        this.datePeremption = datePeremption;
        this.stockActuel = stockActuel;
        this.seuilMinimum = seuilMinimum;
        this.actif = actif;
    }

    // Constructeur sans ID (pour création)
    public Medicament(String nomCommercial, String principeActif, String formeGalenique,
                     String dosage, double prixPublic, boolean necessiteOrdonnance,
                     LocalDate datePeremption, int stockActuel, int seuilMinimum) {
        this.nomCommercial = nomCommercial;
        this.principeActif = principeActif;
        this.formeGalenique = formeGalenique;
        this.dosage = dosage;
        this.prixPublic = prixPublic;
        this.necessiteOrdonnance = necessiteOrdonnance;
        this.datePeremption = datePeremption;
        this.stockActuel = stockActuel;
        this.seuilMinimum = seuilMinimum;
        this.actif = true;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomCommercial() {
        return nomCommercial;
    }

    public void setNomCommercial(String nomCommercial) {
        this.nomCommercial = nomCommercial;
    }

    public String getPrincipeActif() {
        return principeActif;
    }

    public void setPrincipeActif(String principeActif) {
        this.principeActif = principeActif;
    }

    public String getFormeGalenique() {
        return formeGalenique;
    }

    public void setFormeGalenique(String formeGalenique) {
        this.formeGalenique = formeGalenique;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public double getPrixPublic() {
        return prixPublic;
    }

    public void setPrixPublic(double prixPublic) {
        this.prixPublic = prixPublic;
    }

    public boolean isNecessiteOrdonnance() {
        return necessiteOrdonnance;
    }

    public void setNecessiteOrdonnance(boolean necessiteOrdonnance) {
        this.necessiteOrdonnance = necessiteOrdonnance;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    public int getSeuilMinimum() {
        return seuilMinimum;
    }

    public void setSeuilMinimum(int seuilMinimum) {
        this.seuilMinimum = seuilMinimum;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return nomCommercial + " (" + dosage + ") - " + prixPublic + "€";
    }
}
