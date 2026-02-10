package com.pharmacie.dao;

import com.pharmacie.model.Vente;
import com.pharmacie.model.LigneVente;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des ventes en base de données
 */
public class VenteDAO {
    private Connection connection;

    public VenteDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Crée une nouvelle vente avec ses lignes
     */
    public boolean creer(Vente vente) {
        String sqlVente = "INSERT INTO vente (date_heure, avec_ordonnance, montant_total, id_utilisateur) VALUES (?, ?, ?, ?)";
        String sqlLigne = "INSERT INTO ligne_vente (id_vente, id_medicament, quantite, prix_unitaire, sous_total) VALUES (?, ?, ?, ?, ?)";
        
        try {
            connection.setAutoCommit(false);  // Début transaction
            
            // Insérer la vente
            try (PreparedStatement stmtVente = connection.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS)) {
                stmtVente.setTimestamp(1, Timestamp.valueOf(vente.getDateHeure()));
                stmtVente.setBoolean(2, vente.isAvecOrdonnance());
                stmtVente.setDouble(3, vente.getMontantTotal());
                stmtVente.setInt(4, vente.getIdUtilisateur());
                
                stmtVente.executeUpdate();
                
                try (ResultSet generatedKeys = stmtVente.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vente.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            // Insérer les lignes de vente
            try (PreparedStatement stmtLigne = connection.prepareStatement(sqlLigne)) {
                for (LigneVente ligne : vente.getLignesVente()) {
                    stmtLigne.setInt(1, vente.getId());
                    stmtLigne.setInt(2, ligne.getIdMedicament());
                    stmtLigne.setInt(3, ligne.getQuantite());
                    stmtLigne.setDouble(4, ligne.getPrixUnitaire());
                    stmtLigne.setDouble(5, ligne.getSousTotal());
                    stmtLigne.addBatch();
                }
                stmtLigne.executeBatch();
            }
            
            connection.commit();  // Valider transaction
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();  // Annuler transaction en cas d'erreur
            } catch (SQLException ex) {
                System.err.println("Erreur rollback : " + ex.getMessage());
            }
            System.err.println("Erreur lors de la création de la vente : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur setAutoCommit : " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Récupère toutes les ventes
     */
    public List<Vente> lireTous() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente ORDER BY date_heure DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Vente vente = extraireVente(rs);
                vente.setLignesVente(lireLignesVente(vente.getId()));
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des ventes : " + e.getMessage());
        }
        return ventes;
    }

    /**
     * Récupère une vente par son ID avec ses lignes
     */
    public Vente lireParId(int id) {
        String sql = "SELECT * FROM vente WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vente vente = extraireVente(rs);
                    vente.setLignesVente(lireLignesVente(id));
                    return vente;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture de la vente : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère les lignes d'une vente
     */
    private List<LigneVente> lireLignesVente(int idVente) {
        List<LigneVente> lignes = new ArrayList<>();
        String sql = "SELECT lv.*, m.nom_commercial FROM ligne_vente lv " +
                    "JOIN medicament m ON lv.id_medicament = m.id " +
                    "WHERE lv.id_vente = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(new LigneVente(
                        rs.getInt("id"),
                        rs.getInt("id_vente"),
                        rs.getInt("id_medicament"),
                        rs.getString("nom_commercial"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_unitaire")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des lignes de vente : " + e.getMessage());
        }
        return lignes;
    }

    /**
     * Récupère les ventes d'une période
     */
    public List<Vente> lireParPeriode(LocalDateTime debut, LocalDateTime fin) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente WHERE date_heure BETWEEN ? AND ? ORDER BY date_heure DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vente vente = extraireVente(rs);
                    vente.setLignesVente(lireLignesVente(vente.getId()));
                    ventes.add(vente);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des ventes par période : " + e.getMessage());
        }
        return ventes;
    }

    /**
     * Calcule le chiffre d'affaires total
     */
    public double getChiffreAffaires() {
        String sql = "SELECT SUM(montant_total) as total FROM vente";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du CA : " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Extrait un objet Vente depuis un ResultSet
     */
    private Vente extraireVente(ResultSet rs) throws SQLException {
        Vente vente = new Vente(
            rs.getInt("id"),
            rs.getTimestamp("date_heure").toLocalDateTime(),
            rs.getBoolean("avec_ordonnance"),
            rs.getInt("id_utilisateur")
        );
        vente.setMontantTotal(rs.getDouble("montant_total"));
        return vente;
    }
}
