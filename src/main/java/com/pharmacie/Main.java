package com.pharmacie;

import com.pharmacie.dao.DatabaseConnection;
import com.pharmacie.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale de l'application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SGPA - Système de Gestion Pharmacie Avancé");
        
        // Afficher l'écran de connexion
        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }

    /**
     * Méthode appelée lors de la fermeture de l'application
     * Permet de libérer les ressources (connexion BD, etc.)
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Fermeture de l'application...");
        
        // Fermer la connexion à la base de données
        DatabaseConnection.getInstance().closeConnection();
        
        // Appeler la méthode parente
        super.stop();
        
        System.out.println("✓ Application fermée proprement");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
