package com.pharmacie.view;

import com.pharmacie.controller.MedicamentController;
import com.pharmacie.model.Medicament;
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
 * Vue pour la gestion des médicaments
 */
public class MedicamentView {
    private Stage stage;
    private MedicamentController controller;
    private TableView<Medicament> table;
    private ObservableList<Medicament> data;

    public MedicamentView(Stage stage) {
        this.stage = stage;
        this.controller = new MedicamentController();
        this.data = FXCollections.observableArrayList();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Gestion des Médicaments", () -> {
            MainMenuView mainMenu = new MainMenuView(stage);
            mainMenu.show();
        });
        root.setTop(header);

        // Barre de recherche et boutons
        HBox toolbar = createToolbar();
        root.setTop(new VBox(header, toolbar));

        // Table
        table = createTable();
        VBox tableContainer = new VBox(table);
        tableContainer.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        root.setCenter(tableContainer);

        // Charger les données
        chargerMedicaments();

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Médicaments - SGPA");
        stage.show();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(15, 20, 15, 20));
        toolbar.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        TextField txtRecherche = new TextField();
        txtRecherche.setPromptText("Rechercher un médicament...");
        txtRecherche.setPrefWidth(300);
        StyleManager.applyTextFieldStyle(txtRecherche);
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                chargerMedicaments();
            } else {
                rechercher(newVal);
            }
        });

        Button btnAjouter = new Button("Ajouter");
        StyleManager.applyPrimaryButtonStyle(btnAjouter);
        btnAjouter.setOnAction(e -> afficherFormulaireAjout());

        Button btnModifier = new Button("Modifier");
        StyleManager.applyWarningButtonStyle(btnModifier);
        btnModifier.setOnAction(e -> {
            Medicament selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherFormulaireModification(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un médicament à modifier");
            }
        });

        Button btnSupprimer = new Button("Supprimer");
        StyleManager.applyDestructiveButtonStyle(btnSupprimer);
        btnSupprimer.setOnAction(e -> {
            Medicament selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                supprimerMedicament(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un médicament à supprimer");
            }
        });

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> chargerMedicaments());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(txtRecherche, spacer, btnAjouter, btnModifier, btnSupprimer, btnActualiser);
        return toolbar;
    }

    private TableView<Medicament> createTable() {
        TableView<Medicament> table = new TableView<>();
        StyleManager.applyTableStyle(table);
        table.setItems(data);

        TableColumn<Medicament, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Medicament, String> colNom = new TableColumn<>("Nom Commercial");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        colNom.setPrefWidth(200);

        TableColumn<Medicament, String> colPrincipe = new TableColumn<>("Principe Actif");
        colPrincipe.setCellValueFactory(new PropertyValueFactory<>("principeActif"));
        colPrincipe.setPrefWidth(150);

        TableColumn<Medicament, String> colForme = new TableColumn<>("Forme");
        colForme.setCellValueFactory(new PropertyValueFactory<>("formeGalenique"));
        colForme.setPrefWidth(100);

        TableColumn<Medicament, String> colDosage = new TableColumn<>("Dosage");
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colDosage.setPrefWidth(100);

        TableColumn<Medicament, Double> colPrix = new TableColumn<>("Prix (€)");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixPublic"));
        colPrix.setPrefWidth(80);

        TableColumn<Medicament, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActuel"));
        colStock.setPrefWidth(70);

        TableColumn<Medicament, Integer> colSeuil = new TableColumn<>("Seuil");
        colSeuil.setCellValueFactory(new PropertyValueFactory<>("seuilMinimum"));
        colSeuil.setPrefWidth(70);

        TableColumn<Medicament, LocalDate> colPeremption = new TableColumn<>("Péremption");
        colPeremption.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));
        colPeremption.setPrefWidth(120);

        TableColumn<Medicament, Boolean> colOrdonnance = new TableColumn<>("Ordonnance");
        colOrdonnance.setCellValueFactory(new PropertyValueFactory<>("necessiteOrdonnance"));
        colOrdonnance.setPrefWidth(100);
        colOrdonnance.setCellFactory(tc -> new TableCell<Medicament, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "OUI" : "NON");
                    setStyle(item ? "-fx-text-fill: " + StyleManager.DESTRUCTIVE_COLOR + "; -fx-font-weight: bold;" : "-fx-text-fill: " + StyleManager.PRIMARY_COLOR + ";");
                }
            }
        });

        table.getColumns().addAll(colId, colNom, colPrincipe, colForme, colDosage, 
                                  colPrix, colStock, colSeuil, colPeremption, colOrdonnance);

        return table;
    }

    private void chargerMedicaments() {
        data.clear();
        data.addAll(controller.getTousMedicaments());
    }

    private void rechercher(String terme) {
        data.clear();
        data.addAll(controller.rechercherMedicaments(terme));
    }

    private void afficherFormulaireAjout() {
        Dialog<Medicament> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un médicament");
        dialog.setHeaderText("Nouveau médicament");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = createFormulaire(null);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                return extraireMedicamentDuFormulaire(grid, false);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(medicament -> {
            if (medicament != null && controller.ajouterMedicament(medicament)) {
                showAlert(Alert.AlertType.INFORMATION, "Médicament ajouté avec succès");
                chargerMedicaments();
            } else if (medicament != null) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du médicament");
            }
        });
    }

    private void afficherFormulaireModification(Medicament medicament) {
        Dialog<Medicament> dialog = new Dialog<>();
        dialog.setTitle("Modifier un médicament");
        dialog.setHeaderText("Modification de: " + medicament.getNomCommercial());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = createFormulaire(medicament);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                return extraireMedicamentDuFormulaire(grid, true);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(med -> {
            if (med != null) {
                med.setId(medicament.getId());
                if (controller.modifierMedicament(med)) {
                    showAlert(Alert.AlertType.INFORMATION, "Médicament modifié avec succès");
                    chargerMedicaments();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification du médicament");
                }
            }
        });
    }

    private GridPane createFormulaire(Medicament medicament) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtNom = new TextField(medicament != null ? medicament.getNomCommercial() : "");
        StyleManager.applyTextFieldStyle(txtNom);
        TextField txtPrincipe = new TextField(medicament != null ? medicament.getPrincipeActif() : "");
        StyleManager.applyTextFieldStyle(txtPrincipe);
        ComboBox<String> cmbForme = new ComboBox<>(FXCollections.observableArrayList(
            "Comprimé", "Sirop", "Crème", "Gel", "Injectable", "Gélule", "Pommade"));
        cmbForme.setValue(medicament != null ? medicament.getFormeGalenique() : "Comprimé");
        cmbForme.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");
        TextField txtDosage = new TextField(medicament != null ? medicament.getDosage() : "");
        StyleManager.applyTextFieldStyle(txtDosage);
        TextField txtPrix = new TextField(medicament != null ? String.valueOf(medicament.getPrixPublic()) : "");
        StyleManager.applyTextFieldStyle(txtPrix);
        TextField txtStock = new TextField(medicament != null ? String.valueOf(medicament.getStockActuel()) : "0");
        StyleManager.applyTextFieldStyle(txtStock);
        TextField txtSeuil = new TextField(medicament != null ? String.valueOf(medicament.getSeuilMinimum()) : "10");
        StyleManager.applyTextFieldStyle(txtSeuil);
        DatePicker dpPeremption = new DatePicker(medicament != null ? medicament.getDatePeremption() : LocalDate.now().plusYears(2));
        CheckBox chkOrdonnance = new CheckBox();
        chkOrdonnance.setSelected(medicament != null && medicament.isNecessiteOrdonnance());

        grid.add(new Label("Nom Commercial:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Principe Actif:"), 0, 1);
        grid.add(txtPrincipe, 1, 1);
        grid.add(new Label("Forme Galénique:"), 0, 2);
        grid.add(cmbForme, 1, 2);
        grid.add(new Label("Dosage:"), 0, 3);
        grid.add(txtDosage, 1, 3);
        grid.add(new Label("Prix Public (€):"), 0, 4);
        grid.add(txtPrix, 1, 4);
        grid.add(new Label("Stock Actuel:"), 0, 5);
        grid.add(txtStock, 1, 5);
        grid.add(new Label("Seuil Minimum:"), 0, 6);
        grid.add(txtSeuil, 1, 6);
        grid.add(new Label("Date Péremption:"), 0, 7);
        grid.add(dpPeremption, 1, 7);
        grid.add(new Label("Nécessite Ordonnance:"), 0, 8);
        grid.add(chkOrdonnance, 1, 8);

        return grid;
    }

    private Medicament extraireMedicamentDuFormulaire(GridPane grid, boolean avecId) {
        TextField txtNom = (TextField) grid.getChildren().get(1);
        TextField txtPrincipe = (TextField) grid.getChildren().get(3);
        ComboBox<String> cmbForme = (ComboBox<String>) grid.getChildren().get(5);
        TextField txtDosage = (TextField) grid.getChildren().get(7);
        TextField txtPrix = (TextField) grid.getChildren().get(9);
        TextField txtStock = (TextField) grid.getChildren().get(11);
        TextField txtSeuil = (TextField) grid.getChildren().get(13);
        DatePicker dpPeremption = (DatePicker) grid.getChildren().get(15);
        CheckBox chkOrdonnance = (CheckBox) grid.getChildren().get(17);

        // Validation des champs
        try {
            String nom = txtNom.getText().trim();
            String principe = txtPrincipe.getText().trim();
            String dosage = txtDosage.getText().trim();
            
            if (nom.isEmpty() || principe.isEmpty() || dosage.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Tous les champs obligatoires doivent être remplis");
                return null;
            }
            
            double prix = Double.parseDouble(txtPrix.getText().trim());
            if (prix <= 0) {
                showAlert(Alert.AlertType.ERROR, "Le prix doit être strictement positif");
                return null;
            }
            
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                showAlert(Alert.AlertType.ERROR, "Le stock ne peut pas être négatif");
                return null;
            }
            
            int seuil = Integer.parseInt(txtSeuil.getText().trim());
            if (seuil < 0) {
                showAlert(Alert.AlertType.ERROR, "Le seuil minimum ne peut pas être négatif");
                return null;
            }
            
            LocalDate datePeremption = dpPeremption.getValue();
            if (datePeremption == null) {
                showAlert(Alert.AlertType.ERROR, "La date de péremption est obligatoire");
                return null;
            }
            
            if (datePeremption.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "La date de péremption ne peut pas être dans le passé");
                return null;
            }

            return new Medicament(
                nom,
                principe,
                cmbForme.getValue(),
                dosage,
                prix,
                chkOrdonnance.isSelected(),
                datePeremption,
                stock,
                seuil
            );
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format : vérifiez les valeurs numériques");
            return null;
        }
    }

    private void supprimerMedicament(Medicament medicament) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer ce médicament ?");
        alert.setContentText(medicament.getNomCommercial());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (controller.supprimerMedicament(medicament.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Médicament supprimé avec succès");
                    chargerMedicaments();
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
