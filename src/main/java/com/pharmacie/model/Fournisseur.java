package com.pharmacie.model;

/**
 * Classe représentant un fournisseur
 */
public class Fournisseur {
    private int id;
    private String nom;
    private String contact;  // Téléphone ou email
    private String adresse;
    private boolean actif;

    // Constructeur complet
    public Fournisseur(int id, String nom, String contact, String adresse, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.contact = contact;
        this.adresse = adresse;
        this.actif = actif;
    }

    // Constructeur sans ID
    public Fournisseur(String nom, String contact, String adresse) {
        this.nom = nom;
        this.contact = contact;
        this.adresse = adresse;
        this.actif = true;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return nom;
    }
}
