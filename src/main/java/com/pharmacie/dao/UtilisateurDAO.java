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
     * Recycle un utilisateur archivé : met à jour ses infos et le réactive
     * @param login le login de l'utilisateur archivé à recycler
     * @param utilisateur les nouvelles données à appliquer
     * @return true si le recyclage a réussi
     */
    public boolean recyclerArchive(String login, Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ?, nom = ?, prenom = ?, role = ?, actif = TRUE WHERE login = ? AND actif = FALSE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getMotDePasse());
            stmt.setString(2, utilisateur.getNom());
            stmt.setString(3, utilisateur.getPrenom());
            stmt.setString(4, utilisateur.getRole().name());
            stmt.setString(5, login);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du recyclage de l'utilisateur archivé : " + e.getMessage());
        }
        return false;
    }

    /**
     * Vérifie si un login existe déjà en base (actifs ET archivés)
     * car la contrainte UNIQUE s'applique à tous les enregistrements.
     * @param login le login à vérifier
     * @param idExclu l'ID de l'utilisateur à exclure (pour la modification), ou -1 pour ignorer
     * @return 0 si le login est libre, 1 si pris par un utilisateur actif, 2 si pris par un archivé
     */
    public int loginExiste(String login, int idExclu) {
        String sql = "SELECT id, actif FROM utilisateur WHERE login = ? AND id != ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setInt(2, idExclu);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("actif") ? 1 : 2;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du login : " + e.getMessage());
        }
        return 0;
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
