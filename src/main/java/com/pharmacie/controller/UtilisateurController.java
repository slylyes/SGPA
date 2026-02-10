package com.pharmacie.controller;

import com.pharmacie.dao.UtilisateurDAO;
import com.pharmacie.model.Utilisateur;
import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs et authentification
 */
public class UtilisateurController {
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurController() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Authentifie un utilisateur
     */
    public boolean authentifier(String login, String motDePasse) {
        if (login == null || login.trim().isEmpty() || motDePasse == null || motDePasse.trim().isEmpty()) {
            System.err.println("Login et mot de passe obligatoires");
            return false;
        }

        Utilisateur utilisateur = utilisateurDAO.authentifier(login, motDePasse);
        
        if (utilisateur != null) {
            SessionManager.getInstance().setUtilisateurConnecte(utilisateur);
            System.out.println("✓ Connexion réussie : " + utilisateur);
            return true;
        }
        
        System.err.println("✗ Identifiants incorrects");
        return false;
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    public void deconnecter() {
        SessionManager.getInstance().deconnecter();
        System.out.println("✓ Déconnexion réussie");
    }

    /**
     * Ajoute un nouvel utilisateur (réservé au pharmacien)
     */
    public boolean ajouterUtilisateur(Utilisateur utilisateur) {
        // Vérifier les permissions
        if (!SessionManager.getInstance().estPharmacien()) {
            System.err.println("Action réservée au pharmacien");
            return false;
        }

        // Validation
        if (utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
            System.err.println("Le login est obligatoire");
            return false;
        }
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().length() < 6) {
            System.err.println("Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }

        return utilisateurDAO.creer(utilisateur);
    }

    /**
     * Modifie un utilisateur (réservé au pharmacien)
     */
    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        if (!SessionManager.getInstance().estPharmacien()) {
            System.err.println("Action réservée au pharmacien");
            return false;
        }
        return utilisateurDAO.mettreAJour(utilisateur);
    }

    /**
     * Supprime un utilisateur (réservé au pharmacien)
     */
    public boolean supprimerUtilisateur(int id) {
        if (!SessionManager.getInstance().estPharmacien()) {
            System.err.println("Action réservée au pharmacien");
            return false;
        }
        
        // Empêcher la suppression de l'utilisateur connecté
        if (SessionManager.getInstance().getUtilisateurConnecte().getId() == id) {
            System.err.println("Impossible de supprimer l'utilisateur connecté");
            return false;
        }
        
        return utilisateurDAO.supprimer(id);
    }

    /**
     * Récupère tous les utilisateurs (réservé au pharmacien)
     */
    public List<Utilisateur> getTousUtilisateurs() {
        if (!SessionManager.getInstance().estPharmacien()) {
            System.err.println("Action réservée au pharmacien");
            return null;
        }
        return utilisateurDAO.lireTous();
    }

    /**
     * Récupère l'utilisateur connecté
     */
    public Utilisateur getUtilisateurConnecte() {
        return SessionManager.getInstance().getUtilisateurConnecte();
    }
}
