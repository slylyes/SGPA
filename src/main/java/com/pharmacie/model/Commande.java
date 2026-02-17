package com.pharmacie.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private int idFournisseur;
    private String nomFournisseur;  // Pour affichage
    private LocalDate dateCommande;
    private LocalDate dateReception;
    private String statut;  // "EN_ATTENTE", "RECUE", "ANNULEE"
    private List<LigneCommande> lignesCommande;

    // Constructeur complet
    public Commande(int id, int idFournisseur, String nomFournisseur, 
                   LocalDate dateCommande, LocalDate dateReception, String statut) {
        this.id = id;
        this.idFournisseur = idFournisseur;
        this.nomFournisseur = nomFournisseur;
        this.dateCommande = dateCommande;
        this.dateReception = dateReception;
        this.statut = statut;
        this.lignesCommande = new ArrayList<>();
    }

    // Constructeur simplifié
    public Commande(int idFournisseur, String nomFournisseur, LocalDate dateCommande) {
        this.idFournisseur = idFournisseur;
        this.nomFournisseur = nomFournisseur;
        this.dateCommande = dateCommande;
        this.statut = "EN_ATTENTE";
        this.lignesCommande = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDate getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDate dateReception) {
        this.dateReception = dateReception;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public void ajouterLigne(LigneCommande ligne) {
        this.lignesCommande.add(ligne);
    }

    @Override
    public String toString() {
        return "Commande #" + id + " - " + nomFournisseur + " - " + statut;
    }
}
