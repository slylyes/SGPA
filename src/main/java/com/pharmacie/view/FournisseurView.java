package com.pharmacie.view;

import com.pharmacie.controller.CommandeController;
import com.pharmacie.model.Commande;
import com.pharmacie.model.Fournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

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
        chargerFournisseurs();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2196F3;");

        Label titre = new Label("Gestion des Fournisseurs");
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
            Fournisseur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherFormulaireModification(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un fournisseur");
            }
        });

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> {
            Fournisseur selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                supprimerFournisseur(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un fournisseur");
            }
        });

        Button btnActualiser = new Button("Actualiser");
        btnActualiser.setOnAction(e -> chargerFournisseurs());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(spacer, btnAjouter, btnModifier, btnSupprimer, btnActualiser);
        return toolbar;
    }

    private TableView<Fournisseur> createTable() {
        TableView<Fournisseur> table = new TableView<>();
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
        TextField txtContact = new TextField(fournisseur != null ? fournisseur.getContact() : "");
        TextField txtAdresse = new TextField(fournisseur != null ? fournisseur.getAdresse() : "");

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
