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
        root.setStyle("-fx-background-color: #f5f5f5;");

        // En-tête
        HBox header = createHeader();
        root.setTop(header);

        // Toolbar
        HBox toolbar = createToolbar();
        root.setTop(new VBox(header, toolbar));

        // Table
        table = createTable();
        root.setCenter(table);

        // Charger les données
        chargerUtilisateurs();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2196F3;");

        Label titre = new Label("Gestion des Utilisateurs");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: white; -fx-text-fill: #2196F3;");
        btnRetour.setOnAction(e -> {
            MainMenuView mainMenu = new MainMenuView(stage);
            mainMenu.show();
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        header.getChildren().addAll(btnRetour, spacer1, titre, spacer2);
        return header;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 20, 10, 20));
        toolbar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAjouter.setOnAction(e -> afficherFormulaireAjout());

        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnModifier.setOnAction(e -> {
            Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherFormulaireModification(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un utilisateur");
            }
        });

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> {
            Utilisateur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                supprimerUtilisateur(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un utilisateur");
            }
        });

        Button btnActualiser = new Button("Actualiser");
        btnActualiser.setOnAction(e -> chargerUtilisateurs());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(spacer, btnAjouter, btnModifier, btnSupprimer, btnActualiser);
        return toolbar;
    }

    private TableView<Utilisateur> createTable() {
        TableView<Utilisateur> table = new TableView<>();
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
            if (controller.ajouterUtilisateur(utilisateur)) {
                showAlert(Alert.AlertType.INFORMATION, "Utilisateur ajouté avec succès");
                chargerUtilisateurs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout");
            }
        });
    }

    private void afficherFormulaireModification(Utilisateur utilisateur) {
        Dialog<Utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier un utilisateur");

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
            if (controller.modifierUtilisateur(u)) {
                showAlert(Alert.AlertType.INFORMATION, "Utilisateur modifié avec succès");
                chargerUtilisateurs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification");
            }
        });
    }

    private GridPane createFormulaire(Utilisateur utilisateur) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtLogin = new TextField(utilisateur != null ? utilisateur.getLogin() : "");
        PasswordField txtPassword = new PasswordField();
        if (utilisateur != null) txtPassword.setText(utilisateur.getMotDePasse());
        TextField txtNom = new TextField(utilisateur != null ? utilisateur.getNom() : "");
        TextField txtPrenom = new TextField(utilisateur != null ? utilisateur.getPrenom() : "");
        
        ComboBox<Role> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll(Role.values());
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
