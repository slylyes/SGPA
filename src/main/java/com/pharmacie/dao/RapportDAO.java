package com.pharmacie.dao;

import com.pharmacie.model.RapportVente;
import com.pharmacie.model.TopMedicament;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la génération des rapports financiers
 */
public class RapportDAO {
    private Connection connection;

    // Libellés des mois en français
    private static final String[] MOIS_FR = {
        "", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };

    public RapportDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Récupère les ventes agrégées par mois entre deux dates
     */
    public List<RapportVente> getVentesParMoisEntreDates(LocalDate debut, LocalDate fin) {
        List<RapportVente> rapports = new ArrayList<>();
        String sql = "SELECT YEAR(v.date_heure) as annee, MONTH(v.date_heure) as mois, " +
                     "COUNT(DISTINCT v.id) as nombre_ventes, " +
                     "COALESCE(SUM(v.montant_total), 0) as chiffre_affaires, " +
                     "COALESCE(SUM(lv.quantite), 0) as quantite_totale " +
                     "FROM vente v " +
                     "LEFT JOIN ligne_vente lv ON v.id = lv.id_vente " +
                     "WHERE DATE(v.date_heure) BETWEEN ? AND ? " +
                     "GROUP BY YEAR(v.date_heure), MONTH(v.date_heure) " +
                     "ORDER BY annee, mois";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int mois = rs.getInt("mois");
                    rapports.add(new RapportVente(
                        rs.getInt("annee"),
                        mois,
                        MOIS_FR[mois],
                        rs.getInt("nombre_ventes"),
                        rs.getDouble("chiffre_affaires"),
                        rs.getInt("quantite_totale")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du rapport par période : " + e.getMessage());
        }
        return rapports;
    }

    /**
     * Récupère le top des médicaments les plus vendus
     */
    public List<TopMedicament> getTopMedicaments(int limite, LocalDate debut, LocalDate fin) {
        List<TopMedicament> top = new ArrayList<>();
        String sql = "SELECT m.id, m.nom_commercial, " +
                     "SUM(lv.quantite) as quantite_totale, " +
                     "SUM(lv.sous_total) as chiffre_affaires, " +
                     "COUNT(DISTINCT lv.id_vente) as nombre_ventes " +
                     "FROM ligne_vente lv " +
                     "JOIN medicament m ON lv.id_medicament = m.id " +
                     "JOIN vente v ON lv.id_vente = v.id " +
                     "WHERE DATE(v.date_heure) BETWEEN ? AND ? " +
                     "GROUP BY m.id, m.nom_commercial " +
                     "ORDER BY quantite_totale DESC " +
                     "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            stmt.setInt(3, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    top.add(new TopMedicament(
                        rs.getInt("id"),
                        rs.getString("nom_commercial"),
                        rs.getInt("quantite_totale"),
                        rs.getDouble("chiffre_affaires"),
                        rs.getInt("nombre_ventes")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du classement des médicaments : " + e.getMessage());
        }
        return top;
    }

    /**
     * Récupère le chiffre d'affaires total sur une période
     */
    public double getChiffreAffairesPeriode(LocalDate debut, LocalDate fin) {
        String sql = "SELECT COALESCE(SUM(montant_total), 0) as total FROM vente " +
                     "WHERE DATE(date_heure) BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du CA : " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Récupère le nombre total de ventes sur une période
     */
    public int getNombreVentesPeriode(LocalDate debut, LocalDate fin) {
        String sql = "SELECT COUNT(*) as total FROM vente " +
                     "WHERE DATE(date_heure) BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des ventes : " + e.getMessage());
        }
        return 0;
    }
}
