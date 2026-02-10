package com.pharmacie.view;

import com.pharmacie.controller.SessionManager;
import com.pharmacie.controller.UtilisateurController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Vue du menu principal
 */
public class MainMenuView {
    private Stage stage;
    private UtilisateurController utilisateurController;

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.utilisateurController = new UtilisateurController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // En-tête
        HBox header = createHeader();
        root.setTop(header);

        // Menu principal
        VBox menu = createMenu();
        root.setCenter(menu);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2196F3;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titre = new Label("Pharmacie Dauphine");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        Label lblUtilisateur = new Label("Connecté: " + utilisateur.getPrenom() + " " + 
                                        utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
        lblUtilisateur.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        Button btnDeconnexion = new Button("Déconnexion");
        btnDeconnexion.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnDeconnexion.setOnAction(e -> {
            utilisateurController.deconnecter();
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        header.getChildren().addAll(titre, spacer, lblUtilisateur, btnDeconnexion);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(20);
        menu.setPadding(new Insets(40));
        menu.setAlignment(Pos.CENTER);

        Label titre = new Label("Menu Principal");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Boutons du menu
        Button btnMedicaments = createMenuButton("Gestion des Médicaments", "");
        btnMedicaments.setOnAction(e -> {
            try {
                System.out.println("Ouverture de la vue Médicaments...");
                MedicamentView medicamentView = new MedicamentView(stage);
                medicamentView.show();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ouverture de MedicamentView: " + ex.getMessage());
                ex.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir la gestion des médicaments: " + ex.getMessage());
            }
        });

        Button btnVentes = createMenuButton("Gestion des Ventes", "");
        btnVentes.setOnAction(e -> {
            try {
                System.out.println("Ouverture de la vue Ventes...");
                VenteView venteView = new VenteView(stage);
                venteView.show();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ouverture de VenteView: " + ex.getMessage());
                ex.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir la gestion des ventes: " + ex.getMessage());
            }
        });

        Button btnAlertes = createMenuButton("Alertes Stock", "");
        btnAlertes.setOnAction(e -> {
            try {
                System.out.println("Ouverture de la vue Alertes...");
                AlerteView alerteView = new AlerteView(stage);
                alerteView.show();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ouverture de AlerteView: " + ex.getMessage());
                ex.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir les alertes: " + ex.getMessage());
            }
        });

        Button btnCommandes = createMenuButton("Commandes Fournisseurs", "");
        btnCommandes.setOnAction(e -> {
            try {
                System.out.println("Ouverture de la vue Commandes...");
                CommandeView commandeView = new CommandeView(stage);
                commandeView.show();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ouverture de CommandeView: " + ex.getMessage());
                ex.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir les commandes: " + ex.getMessage());
            }
        });

        Button btnFournisseurs = createMenuButton("Gestion des Fournisseurs", "");
        btnFournisseurs.setOnAction(e -> {
            try {
                System.out.println("Ouverture de la vue Fournisseurs...");
                FournisseurView fournisseurView = new FournisseurView(stage);
                fournisseurView.show();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ouverture de FournisseurView: " + ex.getMessage());
                ex.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir les fournisseurs: " + ex.getMessage());
            }
        });

        Button btnUtilisateurs = createMenuButton("Gestion des Utilisateurs", "");
        btnUtilisateurs.setOnAction(e -> {
            try {
                System.out.println("Ouverture de la vue Utilisateurs...");
                UtilisateurView utilisateurView = new UtilisateurView(stage);
                utilisateurView.show();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'ouverture de UtilisateurView: " + ex.getMessage());
                ex.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir les utilisateurs: " + ex.getMessage());
            }
        });

        // Désactiver certains boutons pour les préparateurs
        if (!SessionManager.getInstance().estPharmacien()) {
            btnFournisseurs.setDisable(true);
            btnUtilisateurs.setDisable(true);
            btnCommandes.setDisable(true);
        }

        // Ajouter les boutons à la grille
        grid.add(btnMedicaments, 0, 0);
        grid.add(btnVentes, 1, 0);
        grid.add(btnAlertes, 0, 1);
        grid.add(btnCommandes, 1, 1);
        grid.add(btnFournisseurs, 0, 2);
        grid.add(btnUtilisateurs, 1, 2);

        menu.getChildren().addAll(titre, grid);
        return menu;
    }

    private Button createMenuButton(String text, String icon) {
        Button button = new Button(icon + "\n" + text);
        button.setPrefSize(250, 120);
        button.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; " +
                       "-fx-font-size: 14px; -fx-background-radius: 5; -fx-border-radius: 5;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196F3; -fx-border-width: 2; " +
                          "-fx-font-size: 14px; -fx-background-radius: 5; -fx-border-radius: 5;"));
        
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; " +
                          "-fx-font-size: 14px; -fx-background-radius: 5; -fx-border-radius: 5;"));
        
        return button;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
