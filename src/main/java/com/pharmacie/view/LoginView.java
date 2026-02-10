package com.pharmacie.view;

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
        root.setAlignment(Pos.CENTER);
        StyleManager.applyBackgroundStyle(root);

        // Carte de connexion
        VBox card = StyleManager.createCard("");
        card.setMaxWidth(400);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setSpacing(20);

        // Titre
        Label titre = new Label("Pharmacie Dauphine");
        titre.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 28));
        titre.setStyle("-fx-text-fill: " + StyleManager.PRIMARY_COLOR + ";");

        Label sousTitre = new Label("Système de Gestion Pharmacie Avancé");
        sousTitre.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.NORMAL, 14));
        sousTitre.setStyle("-fx-text-fill: " + StyleManager.TEXT_SECONDARY_COLOR + ";");

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        Label lblLogin = new Label("Login:");
        lblLogin.setFont(Font.font(StyleManager.FONT_FAMILY, StyleManager.FONT_SIZE_NORMAL));
        TextField txtLogin = new TextField();
        txtLogin.setPromptText("Entrez votre login");
        txtLogin.setPrefWidth(250);
        StyleManager.applyTextFieldStyle(txtLogin);

        Label lblPassword = new Label("Mot de passe:");
        lblPassword.setFont(Font.font(StyleManager.FONT_FAMILY, StyleManager.FONT_SIZE_NORMAL));
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Entrez votre mot de passe");
        txtPassword.setPrefWidth(250);
        StyleManager.applyTextFieldStyle(txtPassword);

        form.add(lblLogin, 0, 0);
        form.add(txtLogin, 1, 0);
        form.add(lblPassword, 0, 1);
        form.add(txtPassword, 1, 1);

        // Bouton de connexion
        Button btnConnexion = new Button("Se connecter");
        StyleManager.applyPrimaryButtonStyle(btnConnexion);
        btnConnexion.setPrefWidth(200);

        Label lblMessage = new Label();
        lblMessage.setStyle("-fx-text-fill: " + StyleManager.DESTRUCTIVE_COLOR + "; -fx-font-size: 12px;");

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
        txtLogin.setOnAction(e -> txtPassword.requestFocus());

        card.getChildren().addAll(titre, sousTitre, form, btnConnexion, lblMessage);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Connexion - SGPA");
        stage.show();
    }
}
