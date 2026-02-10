package com.pharmacie.view;

import com.pharmacie.controller.SessionManager;
import com.pharmacie.controller.UtilisateurController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = createHeader();
        root.setTop(header);

        // Menu principal
        VBox menu = createMenu();
        root.setCenter(menu);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Menu Principal - SGPA");
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: " + StyleManager.PRIMARY_COLOR + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titre = new Label("Pharmacie Dauphine");
        titre.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 20));
        titre.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        Label lblUtilisateur = new Label("Connecté: " + utilisateur.getPrenom() + " " + 
                                        utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
        lblUtilisateur.setFont(Font.font(StyleManager.FONT_FAMILY, StyleManager.FONT_SIZE_SMALL));
        lblUtilisateur.setStyle("-fx-text-fill: white;");

        Button btnDeconnexion = new Button("Déconnexion");
        StyleManager.applyDestructiveButtonStyle(btnDeconnexion);
        btnDeconnexion.setOnAction(e -> {
            utilisateurController.deconnecter();
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        header.getChildren().addAll(titre, spacer, lblUtilisateur, btnDeconnexion);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(30);
        menu.setPadding(new Insets(40));
        menu.setAlignment(Pos.CENTER);

        Label titre = StyleManager.createTitleLabel("Menu Principal");

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);

        // Boutons du menu
        Button btnMedicaments = createMenuButton("Gestion des Médicaments");
        btnMedicaments.setOnAction(e -> {
            try {
                MedicamentView medicamentView = new MedicamentView(stage);
                medicamentView.show();
            } catch (Exception ex) {
                showError("Erreur", "Impossible d'ouvrir la gestion des médicaments: " + ex.getMessage());
            }
        });

        Button btnVentes = createMenuButton("Gestion des Ventes");
        btnVentes.setOnAction(e -> {
            try {
                VenteView venteView = new VenteView(stage);
                venteView.show();
            } catch (Exception ex) {
                showError("Erreur", "Impossible d'ouvrir la gestion des ventes: " + ex.getMessage());
            }
        });

        Button btnAlertes = createMenuButton("Alertes Stock");
        btnAlertes.setOnAction(e -> {
            try {
                AlerteView alerteView = new AlerteView(stage);
                alerteView.show();
            } catch (Exception ex) {
                showError("Erreur", "Impossible d'ouvrir les alertes: " + ex.getMessage());
            }
        });

        Button btnCommandes = createMenuButton("Commandes Fournisseurs");
        btnCommandes.setOnAction(e -> {
            try {
                CommandeView commandeView = new CommandeView(stage);
                commandeView.show();
            } catch (Exception ex) {
                showError("Erreur", "Impossible d'ouvrir les commandes: " + ex.getMessage());
            }
        });

        Button btnFournisseurs = createMenuButton("Gestion des Fournisseurs");
        btnFournisseurs.setOnAction(e -> {
            try {
                FournisseurView fournisseurView = new FournisseurView(stage);
                fournisseurView.show();
            } catch (Exception ex) {
                showError("Erreur", "Impossible d'ouvrir les fournisseurs: " + ex.getMessage());
            }
        });

        Button btnUtilisateurs = createMenuButton("Gestion des Utilisateurs");
        btnUtilisateurs.setOnAction(e -> {
            try {
                UtilisateurView utilisateurView = new UtilisateurView(stage);
                utilisateurView.show();
            } catch (Exception ex) {
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

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(280, 120);
        button.setWrapText(true);
        button.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Style de base
        String baseStyle =
            "-fx-font-family: '" + StyleManager.FONT_FAMILY + "'; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: " + StyleManager.PRIMARY_COLOR + "; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);";

        button.setStyle(baseStyle);

        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(baseStyle + "-fx-background-color: " + StyleManager.SECONDARY_COLOR + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        
        button.setOnMouseExited(e -> 
            button.setStyle(baseStyle));
        
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
