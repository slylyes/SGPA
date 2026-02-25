package com.pharmacie.dao;

import com.pharmacie.model.Medicament;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class MedicamentDAO {
    private Connection connection;

    public MedicamentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

   
    public boolean creer(Medicament medicament) {
        String sql = "INSERT INTO medicament (nom_commercial, principe_actif, forme_galenique, " +
                    "dosage, prix_public, necessite_ordonnance, date_peremption, stock_actuel, seuil_minimum, actif) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, medicament.getNomCommercial());
            stmt.setString(2, medicament.getPrincipeActif());
            stmt.setString(3, medicament.getFormeGalenique());
            stmt.setString(4, medicament.getDosage());
            stmt.setDouble(5, medicament.getPrixPublic());
            stmt.setBoolean(6, medicament.isNecessiteOrdonnance());
            stmt.setDate(7, Date.valueOf(medicament.getDatePeremption()));
            stmt.setInt(8, medicament.getStockActuel());
            stmt.setInt(9, medicament.getSeuilMinimum());
            stmt.setBoolean(10, medicament.isActif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medicament.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du médicament : " + e.getMessage());
        }
        return false;
    }

    
    public List<Medicament> lireTous() {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE actif = TRUE ORDER BY nom_commercial";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicaments.add(extraireMedicament(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture des médicaments : " + e.getMessage());
        }
        return medicaments;
    }

    public Medicament lireParId(int id) {
        String sql = "SELECT * FROM medicament WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraireMedicament(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture du médicament : " + e.getMessage());
        }
        return null;
    }

    
    public List<Medicament> rechercherParNom(String nom) {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE actif = TRUE AND nom_commercial LIKE ? ORDER BY nom_commercial";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(extraireMedicament(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return medicaments;
    }

    public boolean mettreAJour(Medicament medicament) {
        String sql = "UPDATE medicament SET nom_commercial = ?, principe_actif = ?, forme_galenique = ?, " +
                    "dosage = ?, prix_public = ?, necessite_ordonnance = ?, date_peremption = ?, " +
                    "stock_actuel = ?, seuil_minimum = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medicament.getNomCommercial());
            stmt.setString(2, medicament.getPrincipeActif());
            stmt.setString(3, medicament.getFormeGalenique());
            stmt.setString(4, medicament.getDosage());
            stmt.setDouble(5, medicament.getPrixPublic());
            stmt.setBoolean(6, medicament.isNecessiteOrdonnance());
            stmt.setDate(7, Date.valueOf(medicament.getDatePeremption()));
            stmt.setInt(8, medicament.getStockActuel());
            stmt.setInt(9, medicament.getSeuilMinimum());
            stmt.setInt(10, medicament.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
        return false;
    }

   
    public boolean supprimer(int id) {
        String sql = "UPDATE medicament SET actif = FALSE WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'archivage : " + e.getMessage());
        }
        return false;
    }

    
    public List<Medicament> getMedicamentsEnAlerteStock() {
        List<Medicament> medicaments = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE actif = TRUE AND stock_actuel <= seuil_minimum ORDER BY stock_actuel";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicaments.add(extraireMedicament(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des alertes : " + e.getMessage());
        }
        return medicaments;
    }

    
    public List<Medicament> getMedicamentsProchesPeremption() {
        List<Medicament> medicaments = new ArrayList<>();
        LocalDate limitDate = LocalDate.now().plusMonths(3);
        String sql = "SELECT * FROM medicament WHERE actif = TRUE AND stock_actuel > 0 AND date_peremption < ? ORDER BY date_peremption";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(limitDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(extraireMedicament(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médicaments périmes : " + e.getMessage());
        }
        return medicaments;
    }

    
    public boolean mettreAJourStock(int idMedicament, int nouvelleQuantite) {
        String sql = "UPDATE medicament SET stock_actuel = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, idMedicament);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
        }
        return false;
    }

    /**
     * Extrait un objet Medicament depuis un ResultSet
     */
    private Medicament extraireMedicament(ResultSet rs) throws SQLException {
        return new Medicament(
            rs.getInt("id"),
            rs.getString("nom_commercial"),
            rs.getString("principe_actif"),
            rs.getString("forme_galenique"),
            rs.getString("dosage"),
            rs.getDouble("prix_public"),
            rs.getBoolean("necessite_ordonnance"),
            rs.getDate("date_peremption").toLocalDate(),
            rs.getInt("stock_actuel"),
            rs.getInt("seuil_minimum"),
            rs.getBoolean("actif")
        );
    }
}
