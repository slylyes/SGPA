package com.pharmacie.dao;

import com.pharmacie.model.Role;
import com.pharmacie.model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des utilisateurs en base de données
 */
public class UtilisateurDAO {
    private Connection connection;

    public UtilisateurDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Authentifie un utilisateur actif
     */
    public Utilisateur authentifier(String login, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE login = ? AND mot_de_passe = ? AND actif = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, motDePasse);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraireUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification : " + e.getMessage());
        }
        return null;
    }

    /**
     * Crée un nouvel utilisateur
     */
    public boolean creer(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (login, mot_de_passe, nom, prenom, role, actif) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getLogin());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getNom());
            stmt.setString(4, utilisateur.getPrenom());
            stmt.setString(5, utilisateur.getRole().name());
            stmt.setBoolean(6, utilisateur.isActif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utilisateur.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupère tous les utilisateurs actifs
     */
    public List<Utilisateur> lireTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE actif = TRUE ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(extraireUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des utilisateurs : " + e.getMessage());
        }
        return utilisateurs;
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public Utilisateur lireParId(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraireUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    /**
     * Met à jour un utilisateur
     */
    public boolean mettreAJour(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET login = ?, mot_de_passe = ?, nom = ?, prenom = ?, role = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getLogin());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getNom());
            stmt.setString(4, utilisateur.getPrenom());
            stmt.setString(5, utilisateur.getRole().name());
            stmt.setInt(6, utilisateur.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
        return false;
    }

    /**
     * Archive un utilisateur (suppression logique)
     */
    public boolean supprimer(int id) {
        String sql = "UPDATE utilisateur SET actif = FALSE WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'archivage de l'utilisateur : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Réactive un utilisateur archivé
     */
    public boolean reactiver(int id) {
        String sql = "UPDATE utilisateur SET actif = TRUE WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réactivation de l'utilisateur : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Récupère tous les utilisateurs archivés
     */
    public List<Utilisateur> lireTousArchives() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE actif = FALSE ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(extraireUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des utilisateurs archivés : " + e.getMessage());
        }
        return utilisateurs;
    }

    /**
     * Extrait un objet Utilisateur depuis un ResultSet
     */
    private Utilisateur extraireUtilisateur(ResultSet rs) throws SQLException {
        return new Utilisateur(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("mot_de_passe"),
            rs.getString("nom"),
            rs.getString("prenom"),
            Role.valueOf(rs.getString("role")),
            rs.getBoolean("actif")
        );
    }
}
