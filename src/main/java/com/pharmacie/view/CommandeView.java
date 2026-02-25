package com.pharmacie.view;

import com.pharmacie.controller.CommandeController;
import com.pharmacie.controller.MedicamentController;
import com.pharmacie.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

/**
 * Vue pour la gestion des commandes fournisseurs
 */
public class CommandeView {
    private Stage stage;
    private CommandeController commandeController;
    private MedicamentController medicamentController;
    private TableView<Commande> table;
    private ObservableList<Commande> data;

    public CommandeView(Stage stage) {
        this.stage = stage;
        this.commandeController = new CommandeController();
        this.medicamentController = new MedicamentController();
        this.data = FXCollections.observableArrayList();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Gestion des Commandes Fournisseurs", () -> {
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
        chargerCommandes();

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Commandes - SGPA");
        stage.show();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(15, 20, 15, 20));
        toolbar.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Button btnNouvelle = new Button("Nouvelle commande");
        StyleManager.applyPrimaryButtonStyle(btnNouvelle);
        btnNouvelle.setOnAction(e -> afficherFormulaireCommande());

        Button btnRecue = new Button("Marquer comme reçue");
        StyleManager.applyWarningButtonStyle(btnRecue);
        // Special color for received? Warning style is orange, maybe custom green?
        // Let's stick to warning for action that changes state important
        btnRecue.setOnAction(e -> {
            Commande selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getStatut().equals("EN_ATTENTE")) {
                recevoirCommande(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez une commande en attente");
            }
        });

        Button btnAuto = new Button("Commande automatique");
        StyleManager.applySecondaryButtonStyle(btnAuto);
        btnAuto.setOnAction(e -> afficherCommandeAutomatique());

        Button btnDetails = new Button("Détails");
        StyleManager.applySecondaryButtonStyle(btnDetails);
        btnDetails.setOnAction(e -> {
            Commande selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherDetailsCommande(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez une commande");
            }
        });

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> chargerCommandes());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(btnNouvelle, btnRecue, btnAuto, spacer, btnDetails, btnActualiser);
        return toolbar;
    }

    private TableView<Commande> createTable() {
        TableView<Commande> table = new TableView<>();
        StyleManager.applyTableStyle(table);
        table.setItems(data);

        TableColumn<Commande, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Commande, String> colFournisseur = new TableColumn<>("Fournisseur");
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("nomFournisseur"));
        colFournisseur.setPrefWidth(250);

        TableColumn<Commande, LocalDate> colDateCmd = new TableColumn<>("Date Commande");
        colDateCmd.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colDateCmd.setPrefWidth(150);

        TableColumn<Commande, LocalDate> colDateRec = new TableColumn<>("Date Réception");
        colDateRec.setCellValueFactory(new PropertyValueFactory<>("dateReception"));
        colDateRec.setPrefWidth(150);

        TableColumn<Commande, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setPrefWidth(120);

        colStatut.setCellFactory(tc -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("RECUE")) {
                        setStyle("-fx-text-fill: " + StyleManager.PRIMARY_COLOR + "; -fx-font-weight: bold;");
                    } else if (item.equals("EN_ATTENTE")) {
                        setStyle("-fx-text-fill: " + StyleManager.WARNING_COLOR + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        table.getColumns().addAll(colId, colFournisseur, colDateCmd, colDateRec, colStatut);

        return table;
    }

    private void chargerCommandes() {
        data.clear();
        data.addAll(commandeController.getToutesCommandes());
    }

    private void afficherFormulaireCommande() {
        Dialog<Commande> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Commande");
        dialog.setHeaderText("Créer une nouvelle commande fournisseur");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ButtonType btnValider = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Sélection du fournisseur
        ComboBox<Fournisseur> cmbFournisseur = new ComboBox<>();
        cmbFournisseur.getItems().addAll(commandeController.getTousFournisseurs());
        cmbFournisseur.setPromptText("Sélectionner un fournisseur");
        cmbFournisseur.setPrefWidth(300);
        cmbFournisseur.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        // Liste des médicaments à commander
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");
        ObservableList<LigneCommande> lignes = FXCollections.observableArrayList();

        ComboBox<Medicament> cmbMedicament = new ComboBox<>();
        cmbMedicament.getItems().addAll(medicamentController.getTousMedicaments());
        cmbMedicament.setPromptText("Sélectionner un médicament");
        cmbMedicament.setPrefWidth(250);
        cmbMedicament.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        Spinner<Integer> spinQte = new Spinner<>(1, 1000, 50);
        spinQte.setPrefWidth(100);

        Button btnAjouterLigne = new Button("Ajouter");
        StyleManager.applyPrimaryButtonStyle(btnAjouterLigne);
        btnAjouterLigne.setOnAction(e -> {
            Medicament med = cmbMedicament.getValue();
            if (med != null) {
                int quantite = spinQte.getValue();
                if (quantite <= 0) {
                    showAlert(Alert.AlertType.ERROR, "La quantité doit être supérieure à 0");
                    return;
                }
                LigneCommande ligne = new LigneCommande(med.getId(), med.getNomCommercial(), quantite);
                lignes.add(ligne);
                listView.getItems().add(med.getNomCommercial() + " x" + quantite);
                cmbMedicament.setValue(null);
                spinQte.getValueFactory().setValue(50);
            } else {
                showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un médicament");
            }
        });

        HBox ligneForm = new HBox(10, cmbMedicament, spinQte, btnAjouterLigne);
        ligneForm.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        content.getChildren().addAll(
            new Label("Fournisseur:"), cmbFournisseur,
            new Separator(),
            new Label("Médicaments à commander:"), ligneForm, listView
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                if (cmbFournisseur.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "Veuillez sélectionner un fournisseur");
                    return null;
                }
                if (lignes.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Veuillez ajouter au moins un médicament à la commande");
                    return null;
                }
                Commande cmd = new Commande(
                    cmbFournisseur.getValue().getId(),
                    cmbFournisseur.getValue().getNom(),
                    LocalDate.now()
                );
                cmd.setLignesCommande(lignes);
                return cmd;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(commande -> {
            if (commandeController.creerCommande(commande)) {
                showAlert(Alert.AlertType.INFORMATION, "Commande créée avec succès");
                chargerCommandes();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la création");
            }
        });
    }

    private void afficherCommandeAutomatique() {
        List<Fournisseur> fournisseurs = commandeController.getTousFournisseurs();
        if (fournisseurs.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucun fournisseur disponible");
            return;
        }

        ChoiceDialog<Fournisseur> dialog = new ChoiceDialog<>(fournisseurs.get(0), fournisseurs);
        dialog.setTitle("Commande Automatique");
        dialog.setHeaderText("Créer une commande automatique pour les médicaments en alerte");
        dialog.setContentText("Choisir le fournisseur:");
        dialog.getDialogPane().setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        dialog.showAndWait().ifPresent(fournisseur -> {
            int nbMedicaments = commandeController.creerCommandesAutomatiques(fournisseur.getId());
            if (nbMedicaments > 0) {
                showAlert(Alert.AlertType.INFORMATION, 
                         "Commande automatique créée avec " + nbMedicaments + " médicaments");
                chargerCommandes();
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Aucun médicament en alerte de stock");
            }
        });
    }

    private void recevoirCommande(Commande commande) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Marquer cette commande comme reçue ?");
        alert.setContentText("Les stocks seront automatiquement mis à jour.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (commandeController.recevoirCommande(commande.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Commande reçue - Stocks mis à jour");
                    chargerCommandes();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la réception");
                }
            }
        });
    }

    private void afficherDetailsCommande(Commande commande) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la commande");
        alert.setHeaderText("Commande #" + commande.getId() + " - " + commande.getNomFournisseur());

        StringBuilder content = new StringBuilder();
        content.append("Date de commande: ").append(commande.getDateCommande()).append("\n");
        content.append("Statut: ").append(commande.getStatut()).append("\n");
        if (commande.getDateReception() != null) {
            content.append("Date de réception: ").append(commande.getDateReception()).append("\n");
        }
        content.append("\nMédicaments commandés:\n");
        content.append("─────────────────────\n");

        for (LigneCommande ligne : commande.getLignesCommande()) {
            content.append(String.format("• %s - Quantité: %d\n", 
                ligne.getNomMedicament(), ligne.getQuantite()));
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
