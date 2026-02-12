package com.pharmacie.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une vente/transaction
 */
public class Vente {
    private int id;
    private LocalDateTime dateHeure;
    private List<LigneVente> lignesVente;
    private double montantTotal;
    private boolean avecOrdonnance;
    private int idUtilisateur;

    // Constructeur complet
    public Vente(int id, LocalDateTime dateHeure, boolean avecOrdonnance, int idUtilisateur) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.avecOrdonnance = avecOrdonnance;
        this.idUtilisateur = idUtilisateur;
        this.lignesVente = new ArrayList<>();
        this.montantTotal = 0.0;
    }

    // Constructeur sans ID (pour création)
    public Vente(LocalDateTime dateHeure, boolean avecOrdonnance, int idUtilisateur) {
        this.dateHeure = dateHeure;
        this.avecOrdonnance = avecOrdonnance;
        this.idUtilisateur = idUtilisateur;
        this.lignesVente = new ArrayList<>();
        this.montantTotal = 0.0;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public List<LigneVente> getLignesVente() {
        return lignesVente;
    }

    public void setLignesVente(List<LigneVente> lignesVente) {
        this.lignesVente = lignesVente;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public boolean isAvecOrdonnance() {
        return avecOrdonnance;
    }

    public void setAvecOrdonnance(boolean avecOrdonnance) {
        this.avecOrdonnance = avecOrdonnance;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    /**
     * Ajoute une ligne de vente et recalcule le montant total
     */
    public void ajouterLigne(LigneVente ligne) {
        this.lignesVente.add(ligne);
        calculerMontantTotal();
    }

    /**
     * Calcule le montant total de la vente
     */
    private void calculerMontantTotal() {
        this.montantTotal = lignesVente.stream()
                .mapToDouble(LigneVente::getSousTotal)
                .sum();
    }

    @Override
    public String toString() {
        return "Vente #" + id + " - " + dateHeure + " - " + montantTotal + "€";
    }
}
