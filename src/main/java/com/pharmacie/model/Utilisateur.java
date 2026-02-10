package com.pharmacie.model;

/**
 * Classe représentant un utilisateur du système
 */
public class Utilisateur {
    private int id;
    private String login;
    private String motDePasse;
    private String nom;
    private String prenom;
    private Role role;
    private boolean actif;

    // Constructeur complet
    public Utilisateur(int id, String login, String motDePasse, String nom, 
                      String prenom, Role role, boolean actif) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = actif;
    }

    // Constructeur sans ID
    public Utilisateur(String login, String motDePasse, String nom, 
                      String prenom, Role role) {
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = true;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /**
     * Vérifie si l'utilisateur est pharmacien (admin)
     */
    public boolean estPharmacien() {
        return this.role == Role.PHARMACIEN;
    }

    /**
     * Vérifie si l'utilisateur est préparateur
     */
    public boolean estPreparateur() {
        return this.role == Role.PREPARATEUR;
    }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + role + ")";
    }
}
