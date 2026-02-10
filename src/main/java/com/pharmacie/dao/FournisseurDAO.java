package com.pharmacie.dao;

import com.pharmacie.model.Fournisseur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des fournisseurs en base de données
 */
public class FournisseurDAO {
    private Connection connection;

    public FournisseurDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Crée un nouveau fournisseur
     */
    public boolean creer(Fournisseur fournisseur) {
        String sql = "INSERT INTO fournisseur (nom, contact, adresse, actif) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getContact());
            stmt.setString(3, fournisseur.getAdresse());
            stmt.setBoolean(4, fournisseur.isActif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        fournisseur.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du fournisseur : " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupère tous les fournisseurs actifs
     */
    public List<Fournisseur> lireTous() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur WHERE actif = TRUE ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                fournisseurs.add(extraireFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }

    /**
     * Récupère un fournisseur par son ID
     */
    public Fournisseur lireParId(int id) {
        String sql = "SELECT * FROM fournisseur WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraireFournisseur(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture du fournisseur : " + e.getMessage());
        }
        return null;
    }

    /**
     * Met à jour un fournisseur
     */
    public boolean mettreAJour(Fournisseur fournisseur) {
        String sql = "UPDATE fournisseur SET nom = ?, contact = ?, adresse = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getContact());
            stmt.setString(3, fournisseur.getAdresse());
            stmt.setInt(4, fournisseur.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du fournisseur : " + e.getMessage());
        }
        return false;
    }

    /**
     * Archive un fournisseur (suppression logique)
     */
    public boolean supprimer(int id) {
        String sql = "UPDATE fournisseur SET actif = FALSE WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'archivage du fournisseur : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Réactive un fournisseur archivé
     */
    public boolean reactiver(int id) {
        String sql = "UPDATE fournisseur SET actif = TRUE WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réactivation du fournisseur : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Récupère tous les fournisseurs archivés
     */
    public List<Fournisseur> lireTousArchives() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur WHERE actif = FALSE ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                fournisseurs.add(extraireFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des fournisseurs archivés : " + e.getMessage());
        }
        return fournisseurs;
    }

    /**
     * Extrait un objet Fournisseur depuis un ResultSet
     */
    private Fournisseur extraireFournisseur(ResultSet rs) throws SQLException {
        return new Fournisseur(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("contact"),
            rs.getString("adresse"),
            rs.getBoolean("actif")
        );
    }
}
