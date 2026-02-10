package com.pharmacie.view;

import com.pharmacie.controller.UtilisateurController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Vue pour l'écran de connexion
 */
public class LoginView {
    private Stage stage;
    private UtilisateurController utilisateurController;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.utilisateurController = new UtilisateurController();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Titre
        Label titre = new Label("Pharmacie Dauphine");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        Label sousTitre = new Label("Système de Gestion Pharmacie Avancé");
        sousTitre.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);

        Label lblLogin = new Label("Login:");
        lblLogin.setStyle("-fx-font-size: 14px;");
        TextField txtLogin = new TextField();
        txtLogin.setPromptText("Entrez votre login");
        txtLogin.setPrefWidth(250);

        Label lblPassword = new Label("Mot de passe:");
        lblPassword.setStyle("-fx-font-size: 14px;");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Entrez votre mot de passe");
        txtPassword.setPrefWidth(250);

        form.add(lblLogin, 0, 0);
        form.add(txtLogin, 1, 0);
        form.add(lblPassword, 0, 1);
        form.add(txtPassword, 1, 1);

        // Bouton de connexion
        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-padding: 10 30;");
        btnConnexion.setPrefWidth(200);

        Label lblMessage = new Label();
        lblMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        // Action du bouton
        btnConnexion.setOnAction(e -> {
            String login = txtLogin.getText();
            String password = txtPassword.getText();

            if (login.isEmpty() || password.isEmpty()) {
                lblMessage.setText("Veuillez remplir tous les champs");
                return;
            }

            if (utilisateurController.authentifier(login, password)) {
                // Ouvrir le menu principal
                MainMenuView mainMenu = new MainMenuView(stage);
                mainMenu.show();
            } else {
                lblMessage.setText("Login ou mot de passe incorrect");
            }
        });

        // Permettre la connexion avec la touche Entrée
        txtPassword.setOnAction(e -> btnConnexion.fire());

        root.getChildren().addAll(titre, sousTitre, form, btnConnexion, lblMessage);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }
}
