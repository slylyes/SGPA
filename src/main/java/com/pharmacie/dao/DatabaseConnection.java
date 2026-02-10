package com.pharmacie.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe Singleton pour gérer la connexion à la base de données MySQL
 * Design Pattern: Singleton
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Paramètres de connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacie_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Root@1234";  // METTEZ ICI VOTRE MOT DE PASSE MYSQL ROOT

    /**
     * Constructeur privé (Pattern Singleton)
     */
    private DatabaseConnection() {
        try {
            // Chargement du driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Connexion à la base de données réussie");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL non trouvé : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    /**
     * Retourne l'instance unique de la connexion (Pattern Singleton)
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Retourne la connexion à la base de données
     */
    public Connection getConnection() {
        try {
            // Vérifier si la connexion est toujours valide
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
