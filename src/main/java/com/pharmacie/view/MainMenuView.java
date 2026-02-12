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

        // Menu principal avec un ScrollPane au cas où l'écran est petit
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox menu = createMenu();
        scrollPane.setContent(menu);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 800); // Taille augmentée
        stage.setScene(scene);
        stage.setTitle("Menu Principal - SGPA");
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background-color: " + StyleManager.PRIMARY_COLOR + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titre = new Label("Pharmacie Dauphine");
        titre.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 24));
        titre.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        Label lblNom = new Label(utilisateur.getPrenom() + " " + utilisateur.getNom());
        lblNom.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 14));
        lblNom.setStyle("-fx-text-fill: white;");

        Label lblRole = new Label(utilisateur.getRole().toString());
        lblRole.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.NORMAL, 12));
        lblRole.setStyle("-fx-text-fill: " + StyleManager.SECONDARY_COLOR + ";");

        userInfo.getChildren().addAll(lblNom, lblRole);

        Button btnDeconnexion = new Button("Déconnexion");
        StyleManager.applyDestructiveButtonStyle(btnDeconnexion);
        btnDeconnexion.setOnAction(e -> {
            utilisateurController.deconnecter();
            LoginView loginView = new LoginView(stage);
            loginView.show();
        });

        header.getChildren().addAll(titre, spacer, userInfo, btnDeconnexion);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(40);
        menu.setPadding(new Insets(50));
        menu.setAlignment(Pos.TOP_CENTER);

        // Message de bienvenue
        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);
        Label lblWelcome = new Label("Bienvenue sur votre tableau de bord");
        lblWelcome.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 28));
        lblWelcome.setStyle("-fx-text-fill: " + StyleManager.PRIMARY_COLOR + ";");

        Label lblSub = new Label("Sélectionnez un module pour commencer");
        lblSub.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.NORMAL, 16));
        lblSub.setStyle("-fx-text-fill: " + StyleManager.TEXT_SECONDARY_COLOR + ";");

        welcomeBox.getChildren().addAll(lblWelcome, lblSub);

        // Grille de boutons
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);
        grid.setAlignment(Pos.CENTER);

        // Boutons du menu
        Button btnMedicaments = StyleManager.createLargeMenuButton(
            "Médicaments",
            "Gérer le stock, les prix et les fiches médicaments"
        );
        btnMedicaments.setOnAction(e -> safeShow(() -> new MedicamentView(stage).show()));

        Button btnVentes = StyleManager.createLargeMenuButton(
            "Ventes",
            "Encaisser des clients, gérer le panier et l'historique"
        );
        btnVentes.setOnAction(e -> safeShow(() -> new VenteView(stage).show()));

        Button btnAlertes = StyleManager.createLargeMenuButton(
            "Alertes",
            "Surveillance des ruptures de stock et péremptions"
        );
        btnAlertes.setOnAction(e -> safeShow(() -> new AlerteView(stage).show()));

        Button btnCommandes = StyleManager.createLargeMenuButton(
            "Commandes",
            "Gérer les réapprovisionnements fournisseurs"
        );
        btnCommandes.setOnAction(e -> safeShow(() -> new CommandeView(stage).show()));

        Button btnFournisseurs = StyleManager.createLargeMenuButton(
            "Fournisseurs",
            "Gérer la base de données des fournisseurs"
        );
        btnFournisseurs.setOnAction(e -> safeShow(() -> new FournisseurView(stage).show()));

        Button btnUtilisateurs = StyleManager.createLargeMenuButton(
            "Utilisateurs",
            "Administration des comptes et accès"
        );
        btnUtilisateurs.setOnAction(e -> safeShow(() -> new UtilisateurView(stage).show()));

        Button btnRapports = StyleManager.createLargeMenuButton(
            "Rapports",
            "Rapports financiers, ventes par mois et export PDF/Excel"
        );
        btnRapports.setOnAction(e -> safeShow(() -> new RapportView(stage).show()));

        // Désactiver certains boutons pour les préparateurs
        if (!SessionManager.getInstance().estPharmacien()) {
            btnFournisseurs.setDisable(true);
            btnUtilisateurs.setDisable(true);
            btnCommandes.setDisable(true);
            btnRapports.setDisable(true);

            // Layout différent pour Préparateur (moins de boutons)
            grid.add(btnMedicaments, 0, 0);
            grid.add(btnVentes, 1, 0);
            grid.add(btnAlertes, 2, 0);
        } else {
            // Layout complet pour Pharmacien (3x3)
            grid.add(btnMedicaments, 0, 0);
            grid.add(btnVentes, 1, 0);
            grid.add(btnAlertes, 2, 0);

            grid.add(btnCommandes, 0, 1);
            grid.add(btnFournisseurs, 1, 1);
            grid.add(btnUtilisateurs, 2, 1);

            grid.add(btnRapports, 0, 2);
        }

        menu.getChildren().addAll(welcomeBox, grid);
        return menu;
    }

    private void safeShow(Runnable showAction) {
        try {
            showAction.run();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Erreur de navigation", "Impossible d'ouvrir le module : " + ex.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
