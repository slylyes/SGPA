package com.pharmacie.view;

import com.pharmacie.controller.UtilisateurController;
import com.pharmacie.model.Role;
import com.pharmacie.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Vue pour la gestion des utilisateurs (réservée au pharmacien)
 */
public class UtilisateurView {
    private Stage stage;
    private UtilisateurController controller;
    private TableView<Utilisateur> table;
    private ObservableList<Utilisateur> data;

    public UtilisateurView(Stage stage) {
        this.stage = stage;
        this.controller = new UtilisateurController();
        this.data = FXCollections.observableArrayList();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Gestion des Utilisateurs", () -> {
            MainMenuView mainMenu = new MainMenuView(stage);
            mainMenu.show();
        });
        root.setTop(header);

        // Toolbar
        HBox toolbar = createToolbar();
        root.setTop(new VBox(header, toolbar));

        // Table
        table = createTable();
        VBox tableContainer = new VBox(table);
        tableContainer.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        root.setCenter(tableContainer);

        // Charger les données
        chargerUtilisateurs();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Utilisateurs - SGPA");
        stage.show();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(15, 20, 15, 20));
        toolbar.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Button btnAjouter = new Button("Ajouter");
        StyleManager.applyPrimaryButtonStyle(btnAjouter);
        btnAjouter.setOnAction(e -> afficherFormulaireAjout());

        Button btnModifier = new Button("Modifier");
        StyleManager.applyWarningButtonStyle(btnModifier);
        btnModifier.setOnAction(e -> {
            Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherFormulaireModification(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un utilisateur");
            }
        });

        Button btnSupprimer = new Button("Supprimer");
        StyleManager.applyDestructiveButtonStyle(btnSupprimer);
        btnSupprimer.setOnAction(e -> {
            Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                supprimerUtilisateur(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un utilisateur");
            }
        });

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> chargerUtilisateurs());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(spacer, btnAjouter, btnModifier, btnSupprimer, btnActualiser);
        return toolbar;
    }

    private TableView<Utilisateur> createTable() {
        TableView<Utilisateur> table = new TableView<>();
        StyleManager.applyTableStyle(table);
        table.setItems(data);

        TableColumn<Utilisateur, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Utilisateur, String> colLogin = new TableColumn<>("Login");
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colLogin.setPrefWidth(150);

        TableColumn<Utilisateur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);

        TableColumn<Utilisateur, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);

        TableColumn<Utilisateur, Role> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(150);

        table.getColumns().addAll(colId, colLogin, colNom, colPrenom, colRole);
        return table;
    }

    private void chargerUtilisateurs() {
        data.clear();
        var utilisateurs = controller.getTousUtilisateurs();
        if (utilisateurs != null) {
            data.addAll(utilisateurs);
        }
    }

    private void afficherFormulaireAjout() {
        Dialog<Utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un utilisateur");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = createFormulaire(null);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                return extraireUtilisateurDuFormulaire(grid);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(utilisateur -> {
            if (utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Le login est obligatoire");
                return;
            }
            if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Le mot de passe doit contenir au moins 6 caractères");
                return;
            }
            // Vérifier le login avant d'appeler le contrôleur pour un message précis
            int loginStatus = controller.verifierLogin(utilisateur.getLogin(), -1);
            if (loginStatus == 1) {
                showAlert(Alert.AlertType.ERROR, "Le login \"" + utilisateur.getLogin() + "\" est déjà utilisé par un utilisateur actif.");
                return;
            } else if (loginStatus == 2) {
                // Demander confirmation pour réutiliser le login archivé
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Login archivé détecté");
                confirm.setHeaderText("Le login \"" + utilisateur.getLogin() + "\" appartient à un utilisateur archivé.");
                confirm.setContentText("Voulez-vous remplacer l'ancien compte par ce nouvel utilisateur ?");
                var result = confirm.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return;
                }
            }
            if (controller.ajouterUtilisateur(utilisateur)) {
                showAlert(Alert.AlertType.INFORMATION, "Utilisateur ajouté avec succès");
                chargerUtilisateurs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de l'utilisateur.");
            }
        });
    }

    private void afficherFormulaireModification(Utilisateur utilisateur) {
        Dialog<Utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier un utilisateur");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = createFormulaire(utilisateur);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                return extraireUtilisateurDuFormulaire(grid);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            u.setId(utilisateur.getId());
            if (u.getLogin() == null || u.getLogin().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Le login est obligatoire");
                return;
            }
            if (u.getMotDePasse() == null || u.getMotDePasse().length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Le mot de passe doit contenir au moins 6 caractères");
                return;
            }
            // Vérifier le login avant d'appeler le contrôleur pour un message précis
            int loginStatus = controller.verifierLogin(u.getLogin(), u.getId());
            if (loginStatus == 1) {
                showAlert(Alert.AlertType.ERROR, "Le login \"" + u.getLogin() + "\" est déjà utilisé par un autre utilisateur actif.");
                return;
            } else if (loginStatus == 2) {
                showAlert(Alert.AlertType.ERROR, "Le login \"" + u.getLogin() + "\" est déjà utilisé par un utilisateur archivé.\n\nRéactivez-le depuis la gestion des archives ou choisissez un autre login.");
                return;
            }
            if (controller.modifierUtilisateur(u)) {
                showAlert(Alert.AlertType.INFORMATION, "Utilisateur modifié avec succès");
                chargerUtilisateurs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification de l'utilisateur.");
            }
        });
    }

    private GridPane createFormulaire(Utilisateur utilisateur) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtLogin = new TextField(utilisateur != null ? utilisateur.getLogin() : "");
        StyleManager.applyTextFieldStyle(txtLogin);

        PasswordField txtPassword = new PasswordField();
        StyleManager.applyTextFieldStyle(txtPassword);
        if (utilisateur != null) txtPassword.setText(utilisateur.getMotDePasse());

        TextField txtNom = new TextField(utilisateur != null ? utilisateur.getNom() : "");
        StyleManager.applyTextFieldStyle(txtNom);

        TextField txtPrenom = new TextField(utilisateur != null ? utilisateur.getPrenom() : "");
        StyleManager.applyTextFieldStyle(txtPrenom);
        
        ComboBox<Role> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll(Role.values());
        cmbRole.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");
        if (utilisateur != null) {
            cmbRole.setValue(utilisateur.getRole());
        } else {
            cmbRole.setValue(Role.PREPARATEUR);
        }

        grid.add(new Label("Login:"), 0, 0);
        grid.add(txtLogin, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(new Label("Nom:"), 0, 2);
        grid.add(txtNom, 1, 2);
        grid.add(new Label("Prénom:"), 0, 3);
        grid.add(txtPrenom, 1, 3);
        grid.add(new Label("Rôle:"), 0, 4);
        grid.add(cmbRole, 1, 4);

        return grid;
    }

    private Utilisateur extraireUtilisateurDuFormulaire(GridPane grid) {
        TextField txtLogin = (TextField) grid.getChildren().get(1);
        PasswordField txtPassword = (PasswordField) grid.getChildren().get(3);
        TextField txtNom = (TextField) grid.getChildren().get(5);
        TextField txtPrenom = (TextField) grid.getChildren().get(7);
        ComboBox<Role> cmbRole = (ComboBox<Role>) grid.getChildren().get(9);

        return new Utilisateur(
            txtLogin.getText(),
            txtPassword.getText(),
            txtNom.getText(),
            txtPrenom.getText(),
            cmbRole.getValue()
        );
    }

    private void supprimerUtilisateur(Utilisateur utilisateur) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer cet utilisateur ?");
        alert.setContentText(utilisateur.getPrenom() + " " + utilisateur.getNom());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (controller.supprimerUtilisateur(utilisateur.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Utilisateur supprimé");
                    chargerUtilisateurs();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
