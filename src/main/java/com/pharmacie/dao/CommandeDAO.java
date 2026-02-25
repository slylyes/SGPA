package com.pharmacie.dao;

import com.pharmacie.model.Commande;
import com.pharmacie.model.LigneCommande;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CommandeDAO {
    private Connection connection;

    public CommandeDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    
    public boolean creer(Commande commande) {
        String sqlCommande = "INSERT INTO commande (id_fournisseur, date_commande, statut) VALUES (?, ?, ?)";
        String sqlLigne = "INSERT INTO ligne_commande (id_commande, id_medicament, quantite) VALUES (?, ?, ?)";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmtCommande = connection.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmtCommande.setInt(1, commande.getIdFournisseur());
                stmtCommande.setDate(2, Date.valueOf(commande.getDateCommande()));
                stmtCommande.setString(3, commande.getStatut());
                
                stmtCommande.executeUpdate();
                
                try (ResultSet generatedKeys = stmtCommande.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        commande.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            try (PreparedStatement stmtLigne = connection.prepareStatement(sqlLigne)) {
                for (LigneCommande ligne : commande.getLignesCommande()) {
                    stmtLigne.setInt(1, commande.getId());
                    stmtLigne.setInt(2, ligne.getIdMedicament());
                    stmtLigne.setInt(3, ligne.getQuantite());
                    stmtLigne.addBatch();
                }
                stmtLigne.executeBatch();
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur rollback : " + ex.getMessage());
            }
            System.err.println("Erreur lors de la création de la commande : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur setAutoCommit : " + e.getMessage());
            }
        }
        return false;
    }

   
    public List<Commande> lireTous() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, f.nom as nom_fournisseur FROM commande c " +
                    "JOIN fournisseur f ON c.id_fournisseur = f.id " +
                    "ORDER BY c.date_commande DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande commande = extraireCommande(rs);
                commande.setLignesCommande(lireLignesCommande(commande.getId()));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des commandes : " + e.getMessage());
        }
        return commandes;
    }

   
    public Commande lireParId(int id) {
        String sql = "SELECT c.*, f.nom as nom_fournisseur FROM commande c " +
                    "JOIN fournisseur f ON c.id_fournisseur = f.id " +
                    "WHERE c.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = extraireCommande(rs);
                    commande.setLignesCommande(lireLignesCommande(id));
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture de la commande : " + e.getMessage());
        }
        return null;
    }

   
    private List<LigneCommande> lireLignesCommande(int idCommande) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT lc.*, m.nom_commercial FROM ligne_commande lc " +
                    "JOIN medicament m ON lc.id_medicament = m.id " +
                    "WHERE lc.id_commande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(new LigneCommande(
                        rs.getInt("id"),
                        rs.getInt("id_commande"),
                        rs.getInt("id_medicament"),
                        rs.getString("nom_commercial"),
                        rs.getInt("quantite")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des lignes de commande : " + e.getMessage());
        }
        return lignes;
    }

  
    public boolean marquerCommeRecue(int idCommande) {
        String sql = "UPDATE commande SET statut = 'RECUE', date_reception = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, idCommande);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
        return false;
    }

    
    private Commande extraireCommande(ResultSet rs) throws SQLException {
        LocalDate dateReception = null;
        Date sqlDateReception = rs.getDate("date_reception");
        if (sqlDateReception != null) {
            dateReception = sqlDateReception.toLocalDate();
        }
        
        return new Commande(
            rs.getInt("id"),
            rs.getInt("id_fournisseur"),
            rs.getString("nom_fournisseur"),
            rs.getDate("date_commande").toLocalDate(),
            dateReception,
            rs.getString("statut")
        );
    }
}
