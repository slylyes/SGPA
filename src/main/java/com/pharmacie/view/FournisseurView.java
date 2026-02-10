package com.pharmacie.view;

import com.pharmacie.controller.CommandeController;
import com.pharmacie.model.Fournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Vue pour la gestion des fournisseurs
 */
public class FournisseurView {
    private Stage stage;
    private CommandeController controller;
    private TableView<Fournisseur> table;
    private ObservableList<Fournisseur> data;

    public FournisseurView(Stage stage) {
        this.stage = stage;
        this.controller = new CommandeController();
        this.data = FXCollections.observableArrayList();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Gestion des Fournisseurs", () -> {
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
        chargerFournisseurs();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Fournisseurs - SGPA");
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
            Fournisseur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherFormulaireModification(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un fournisseur");
            }
        });

        Button btnSupprimer = new Button("Supprimer");
        StyleManager.applyDestructiveButtonStyle(btnSupprimer);
        btnSupprimer.setOnAction(e -> {
            Fournisseur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                supprimerFournisseur(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un fournisseur");
            }
        });

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> chargerFournisseurs());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(spacer, btnAjouter, btnModifier, btnSupprimer, btnActualiser);
        return toolbar;
    }

    private TableView<Fournisseur> createTable() {
        TableView<Fournisseur> table = new TableView<>();
        StyleManager.applyTableStyle(table);
        table.setItems(data);

        TableColumn<Fournisseur, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Fournisseur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(250);

        TableColumn<Fournisseur, String> colContact = new TableColumn<>("Contact");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colContact.setPrefWidth(200);

        TableColumn<Fournisseur, String> colAdresse = new TableColumn<>("Adresse");
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colAdresse.setPrefWidth(350);

        table.getColumns().addAll(colId, colNom, colContact, colAdresse);
        return table;
    }

    private void chargerFournisseurs() {
        data.clear();
        data.addAll(controller.getTousFournisseurs());
    }

    private void afficherFormulaireAjout() {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un fournisseur");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = createFormulaire(null);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                return extraireFournisseurDuFormulaire(grid);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(fournisseur -> {
            if (controller.ajouterFournisseur(fournisseur)) {
                showAlert(Alert.AlertType.INFORMATION, "Fournisseur ajouté avec succès");
                chargerFournisseurs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout");
            }
        });
    }

    private void afficherFormulaireModification(Fournisseur fournisseur) {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle("Modifier un fournisseur");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = createFormulaire(fournisseur);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                return extraireFournisseurDuFormulaire(grid);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(f -> {
            f.setId(fournisseur.getId());
            if (controller.modifierFournisseur(f)) {
                showAlert(Alert.AlertType.INFORMATION, "Fournisseur modifié avec succès");
                chargerFournisseurs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification");
            }
        });
    }

    private GridPane createFormulaire(Fournisseur fournisseur) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtNom = new TextField(fournisseur != null ? fournisseur.getNom() : "");
        txtNom.setPrefWidth(300);
        StyleManager.applyTextFieldStyle(txtNom);
        TextField txtContact = new TextField(fournisseur != null ? fournisseur.getContact() : "");
        StyleManager.applyTextFieldStyle(txtContact);
        TextField txtAdresse = new TextField(fournisseur != null ? fournisseur.getAdresse() : "");
        StyleManager.applyTextFieldStyle(txtAdresse);

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Contact:"), 0, 1);
        grid.add(txtContact, 1, 1);
        grid.add(new Label("Adresse:"), 0, 2);
        grid.add(txtAdresse, 1, 2);

        return grid;
    }

    private Fournisseur extraireFournisseurDuFormulaire(GridPane grid) {
        TextField txtNom = (TextField) grid.getChildren().get(1);
        TextField txtContact = (TextField) grid.getChildren().get(3);
        TextField txtAdresse = (TextField) grid.getChildren().get(5);

        return new Fournisseur(txtNom.getText(), txtContact.getText(), txtAdresse.getText());
    }

    private void supprimerFournisseur(Fournisseur fournisseur) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer ce fournisseur ?");
        alert.setContentText(fournisseur.getNom());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (controller.supprimerFournisseur(fournisseur.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Fournisseur supprimé");
                    chargerFournisseurs();
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
