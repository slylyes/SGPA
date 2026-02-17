package com.pharmacie.view;

import com.pharmacie.controller.MedicamentController;
import com.pharmacie.controller.SessionManager;
import com.pharmacie.controller.VenteController;
import com.pharmacie.model.LigneVente;
import com.pharmacie.model.Medicament;
import com.pharmacie.model.Vente;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue pour la gestion des ventes
 */
public class VenteView {
    private Stage stage;
    private VenteController venteController;
    private MedicamentController medicamentController;
    private ObservableList<LigneVente> panierData;
    private TableView<LigneVente> tablePanier;
    private Label lblTotal;

    public VenteView(Stage stage) {
        this.stage = stage;
        this.venteController = new VenteController();
        this.medicamentController = new MedicamentController();
        this.panierData = FXCollections.observableArrayList();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Nouvelle Vente", () -> {
            MainMenuView mainMenu = new MainMenuView(stage);
            mainMenu.show();
        });

        Button btnHistorique = new Button("Historique");
        StyleManager.applySecondaryButtonStyle(btnHistorique);
        // Special style for header button
        btnHistorique.setStyle(btnHistorique.getStyle() + "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-color: transparent;");
        btnHistorique.setOnAction(e -> afficherHistorique());

        header.getChildren().add(btnHistorique); // Add to the right

        root.setTop(header);

        // Contenu principal
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(createSelectionMedicaments(), createPanier());
        splitPane.setDividerPositions(0.4); // More space for basket
        splitPane.setStyle("-fx-background-color: transparent; -fx-padding: 20;");
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Vente - SGPA");
        stage.show();
    }

    private VBox createSelectionMedicaments() {
        VBox box = StyleManager.createCard("Sélection des Médicaments");

        // ComboBox recherchable unique (tapez pour filtrer)
        ComboBox<Medicament> cmbMedicaments = new ComboBox<>();
        cmbMedicaments.setEditable(true);
        cmbMedicaments.setPromptText("Tapez pour rechercher un médicament...");
        cmbMedicaments.setMaxWidth(Double.MAX_VALUE);
        cmbMedicaments.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        ObservableList<Medicament> tousLesMedicaments = FXCollections.observableArrayList(
            medicamentController.getTousMedicaments()
        );
        cmbMedicaments.setItems(tousLesMedicaments);

        // Affichage détaillé dans la liste déroulante
        cmbMedicaments.setCellFactory(lv -> new ListCell<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                    item.getNomCommercial() + "  (" + String.format("%.2f €", item.getPrixPublic()) +
                    " | Stock: " + item.getStockActuel() + ")");
            }
        });

        // Conversion texte ↔ objet
        cmbMedicaments.setConverter(new javafx.util.StringConverter<Medicament>() {
            @Override
            public String toString(Medicament med) {
                return med == null ? "" : med.getNomCommercial();
            }
            @Override
            public Medicament fromString(String s) {
                if (s == null || s.isEmpty()) return null;
                return tousLesMedicaments.stream()
                    .filter(m -> m.getNomCommercial().equalsIgnoreCase(s))
                    .findFirst().orElse(null);
            }
        });

        // Filtrage dynamique en tapant
        cmbMedicaments.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            Medicament selected = cmbMedicaments.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getNomCommercial().equals(newVal)) return;

            javafx.application.Platform.runLater(() -> {
                if (newVal == null || newVal.isEmpty()) {
                    cmbMedicaments.setItems(tousLesMedicaments);
                } else {
                    ObservableList<Medicament> filtre = FXCollections.observableArrayList();
                    String recherche = newVal.toLowerCase();
                    for (Medicament m : tousLesMedicaments) {
                        if (m.getNomCommercial().toLowerCase().contains(recherche)) {
                            filtre.add(m);
                        }
                    }
                    cmbMedicaments.setItems(filtre);
                }
                if (!cmbMedicaments.isShowing()) {
                    cmbMedicaments.show();
                }
            });
        });

        Label lblStock = new Label("Stock disponible: -");
        lblStock.setStyle("-fx-font-size: 12px; -fx-text-fill: " + StyleManager.TEXT_SECONDARY_COLOR + ";");

        cmbMedicaments.setOnAction(e -> {
            Medicament selected = cmbMedicaments.getValue();
            if (selected != null) {
                lblStock.setText("Stock disponible: " + selected.getStockActuel() + " unités");
            }
        });

        Spinner<Integer> spinQuantite = new Spinner<>(1, 100, 1);
        spinQuantite.setEditable(true);
        spinQuantite.setPrefWidth(100);
        spinQuantite.getEditor().setFont(Font.font(StyleManager.FONT_FAMILY, 14));

        Button btnAjouter = new Button("Ajouter au panier");
        StyleManager.applyPrimaryButtonStyle(btnAjouter);
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setOnAction(e -> {
            Medicament selected = cmbMedicaments.getValue();
            if (selected != null) {
                ajouterAuPanier(selected, spinQuantite.getValue());
                cmbMedicaments.setValue(null);
                cmbMedicaments.getEditor().clear();
                cmbMedicaments.setItems(tousLesMedicaments);
                spinQuantite.getValueFactory().setValue(1);
                lblStock.setText("Stock disponible: -");
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélectionnez un médicament");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.add(new Label("Médicament:"), 0, 0);
        form.add(cmbMedicaments, 1, 0);
        form.add(lblStock, 1, 1);
        form.add(new Label("Quantité:"), 0, 2);
        form.add(spinQuantite, 1, 2);

        box.getChildren().addAll(form, new Separator(), btnAjouter);
        return box;
    }

    private VBox createPanier() {
        VBox box = StyleManager.createCard("Panier");

        tablePanier = new TableView<>();
        StyleManager.applyTableStyle(tablePanier);
        tablePanier.setItems(panierData);

        TableColumn<LigneVente, String> colNom = new TableColumn<>("Médicament");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));

        TableColumn<LigneVente, Integer> colQte = new TableColumn<>("Quantité");
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        TableColumn<LigneVente, Double> colPrixU = new TableColumn<>("Prix Unit.");
        colPrixU.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        TableColumn<LigneVente, Double> colTotal = new TableColumn<>("Sous-Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));

        tablePanier.getColumns().clear();
        tablePanier.getColumns().addAll(List.of(colNom, colQte, colPrixU, colTotal));

        Button btnRetirer = new Button("Retirer du panier");
        StyleManager.applyDestructiveButtonStyle(btnRetirer);
        btnRetirer.setOnAction(e -> {
            LigneVente selected = tablePanier.getSelectionModel().getSelectedItem();
            if (selected != null) {
                panierData.remove(selected);
                calculerTotal();
            }
        });

        CheckBox chkOrdonnance = new CheckBox("Vente avec ordonnance");
        chkOrdonnance.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        lblTotal = new Label("TOTAL: 0.00 €");
        lblTotal.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 24));
        lblTotal.setStyle("-fx-text-fill: " + StyleManager.PRIMARY_COLOR + ";");

        Button btnValider = new Button("Valider la vente");
        StyleManager.applyPrimaryButtonStyle(btnValider);
        btnValider.setPrefWidth(200);
        btnValider.setStyle("-fx-font-size: 16px; -fx-background-color: " + StyleManager.PRIMARY_COLOR + "; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 8;");

        btnValider.setOnAction(e -> {
            if (validerVente(chkOrdonnance.isSelected())) {
                panierData.clear();
                calculerTotal();
            }
        });

        Button btnVider = new Button("Vider");
        StyleManager.applySecondaryButtonStyle(btnVider);
        btnVider.setOnAction(e -> {
            panierData.clear();
            calculerTotal();
        });

        HBox actions = new HBox(10, btnRetirer, btnVider);

        HBox totalBox = new HBox(lblTotal);
        totalBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        HBox validationBox = new HBox(btnValider);
        validationBox.setAlignment(javafx.geometry.Pos.CENTER);

        box.getChildren().addAll(tablePanier, actions, new Separator(),
                                chkOrdonnance, totalBox, validationBox);
        VBox.setVgrow(tablePanier, Priority.ALWAYS);
        return box;
    }

    private void ajouterAuPanier(Medicament medicament, int quantite) {
        // Validation de la quantité
        if (quantite <= 0) {
            showAlert(Alert.AlertType.ERROR, "La quantité doit être supérieure à 0");
            return;
        }

        // Vérification de la date de péremption
        if (medicament.getDatePeremption().isBefore(java.time.LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, 
                "VENTE INTERDITE : Le médicament \"" + medicament.getNomCommercial() + 
                "\" est périmé depuis le " + medicament.getDatePeremption() + " !");
            return;
        }
        
        if (medicament.getStockActuel() < quantite) {
            showAlert(Alert.AlertType.ERROR, "Stock insuffisant ! Disponible: " + medicament.getStockActuel());
            return;
        }

        // Vérifier si le médicament est déjà dans le panier
        for (LigneVente ligne : panierData) {
            if (ligne.getIdMedicament() == medicament.getId()) {
                int nouvelleQuantite = ligne.getQuantite() + quantite;
                if (medicament.getStockActuel() < nouvelleQuantite) {
                    showAlert(Alert.AlertType.ERROR, "Stock insuffisant pour cette quantité totale !");
                    return;
                }
                ligne.setQuantite(nouvelleQuantite);
                tablePanier.refresh();
                calculerTotal();
                return;
            }
        }

        // Ajouter une nouvelle ligne
        LigneVente ligne = new LigneVente(
            medicament.getId(),
            medicament.getNomCommercial(),
            quantite,
            medicament.getPrixPublic()
        );
        panierData.add(ligne);
        calculerTotal();
    }

    private void calculerTotal() {
        double total = panierData.stream()
            .mapToDouble(LigneVente::getSousTotal)
            .sum();
        lblTotal.setText(String.format("TOTAL: %.2f €", total));
    }

    private boolean validerVente(boolean avecOrdonnance) {
        if (panierData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Le panier est vide");
            return false;
        }

        // Vérifier la péremption des médicaments
        List<String> medicamentsPerimes = new ArrayList<>();
        for (LigneVente ligne : panierData) {
            Medicament med = medicamentController.getMedicamentParId(ligne.getIdMedicament());
            if (med != null && med.getDatePeremption().isBefore(java.time.LocalDate.now())) {
                medicamentsPerimes.add(med.getNomCommercial() + " (périmé le " + med.getDatePeremption() + ")");
            }
        }
        if (!medicamentsPerimes.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,
                "VENTE INTERDITE : Les médicaments suivants sont périmés :\n" +
                String.join("\n", medicamentsPerimes) +
                "\n\nRetirez-les du panier avant de valider.");
            return false;
        }

        // Vérifier si des médicaments nécessitent une ordonnance
        List<String> medicamentsOrdonnance = new ArrayList<>();
        for (LigneVente ligne : panierData) {
            Medicament med = medicamentController.getMedicamentParId(ligne.getIdMedicament());
            if (med != null && med.isNecessiteOrdonnance()) {
                medicamentsOrdonnance.add(med.getNomCommercial());
            }
        }
        
        if (!medicamentsOrdonnance.isEmpty() && !avecOrdonnance) {
            showAlert(Alert.AlertType.ERROR, 
                "VENTE INTERDITE : Les médicaments suivants nécessitent une ordonnance :\n" + 
                String.join("\n", medicamentsOrdonnance) + 
                "\n\nVeuillez cocher 'Vente avec ordonnance'");
            return false;
        }

        Vente vente = new Vente(
            LocalDateTime.now(),
            avecOrdonnance,
            SessionManager.getInstance().getUtilisateurConnecte().getId()
        );

        for (LigneVente ligne : panierData) {
            vente.ajouterLigne(ligne);
        }

        if (venteController.enregistrerVente(vente)) {
            showAlert(Alert.AlertType.INFORMATION, 
                     String.format("Vente enregistrée avec succès !\nMontant: %.2f €", vente.getMontantTotal()));
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement de la vente");
            return false;
        }
    }

    private void afficherHistorique() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Historique des ventes");
        dialogStage.initOwner(stage);

        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // Données de base
        ObservableList<Vente> ventesBase = FXCollections.observableArrayList(venteController.getToutesVentes());
        FilteredList<Vente> ventesFiltrees = new FilteredList<>(ventesBase, v -> true);

        // Filtres
        TextField txtRecherche = new TextField();
        txtRecherche.setPromptText("ID vente ou médicament...");
        txtRecherche.setPrefWidth(200);
        StyleManager.applyTextFieldStyle(txtRecherche);

        DatePicker dpDebut = new DatePicker();
        dpDebut.setPromptText("Date début");
        DatePicker dpFin = new DatePicker();
        dpFin.setPromptText("Date fin");

        ComboBox<String> cmbOrdonnance = new ComboBox<>();
        cmbOrdonnance.getItems().addAll("Toutes", "Avec ordonnance", "Sans ordonnance");
        cmbOrdonnance.setValue("Toutes");
        cmbOrdonnance.setStyle("-fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> {
            ventesBase.setAll(venteController.getToutesVentes());
            appliquerFiltres(ventesFiltrees, txtRecherche, dpDebut, dpFin, cmbOrdonnance);
        });

        HBox filtres = new HBox(10, new Label("Recherche:"), txtRecherche,
                                new Label("Début:"), dpDebut,
                                new Label("Fin:"), dpFin,
                                new Label("Ordonnance:"), cmbOrdonnance,
                                btnActualiser);
        filtres.setPadding(new Insets(10, 20, 10, 20));
        filtres.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");
        filtres.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Table des ventes
        TableView<Vente> tableVentes = new TableView<>();
        StyleManager.applyTableStyle(tableVentes);
        tableVentes.setItems(ventesFiltrees);

        TableColumn<Vente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Vente, String> colDate = new TableColumn<>("Date/Heure");
        colDate.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDateHeure().toString()));
        colDate.setPrefWidth(180);

        TableColumn<Vente, String> colOrd = new TableColumn<>("Ordonnance");
        colOrd.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isAvecOrdonnance() ? "Oui" : "Non"));
        colOrd.setPrefWidth(110);

        TableColumn<Vente, Double> colMontant = new TableColumn<>("Montant");
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colMontant.setPrefWidth(120);

        tableVentes.getColumns().clear();
        tableVentes.getColumns().addAll(List.of(colId, colDate, colOrd, colMontant));

        // Table des lignes de vente
        TableView<LigneVente> tableLignes = new TableView<>();
        StyleManager.applyTableStyle(tableLignes);

        TableColumn<LigneVente, String> colMed = new TableColumn<>("Médicament");
        colMed.setCellValueFactory(new PropertyValueFactory<>("nomMedicament"));
        colMed.setPrefWidth(220);

        TableColumn<LigneVente, Integer> colQte = new TableColumn<>("Quantité");
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colQte.setPrefWidth(80);

        TableColumn<LigneVente, Double> colPU = new TableColumn<>("Prix Unit.");
        colPU.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colPU.setPrefWidth(90);

        TableColumn<LigneVente, Double> colST = new TableColumn<>("Sous-Total");
        colST.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));
        colST.setPrefWidth(100);

        tableLignes.getColumns().clear();
        tableLignes.getColumns().addAll(List.of(colMed, colQte, colPU, colST));

        Label lblDetails = new Label("Détails des médicaments vendus");
        lblDetails.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 5 0; -fx-font-family: '" + StyleManager.FONT_FAMILY + "';");

        VBox detailsBox = new VBox(5, lblDetails, tableLignes);
        detailsBox.setPadding(new Insets(10));
        VBox.setVgrow(tableLignes, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(tableVentes, detailsBox);
        splitPane.setDividerPositions(0.55);

        // Sélection vente -> afficher lignes
        tableVentes.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && newV.getLignesVente() != null) {
                tableLignes.setItems(FXCollections.observableArrayList(newV.getLignesVente()));
            } else {
                tableLignes.setItems(FXCollections.observableArrayList());
            }
        });

        // Événements filtres
        txtRecherche.textProperty().addListener((obs, o, n) ->
            appliquerFiltres(ventesFiltrees, txtRecherche, dpDebut, dpFin, cmbOrdonnance));
        dpDebut.valueProperty().addListener((obs, o, n) ->
            appliquerFiltres(ventesFiltrees, txtRecherche, dpDebut, dpFin, cmbOrdonnance));
        dpFin.valueProperty().addListener((obs, o, n) ->
            appliquerFiltres(ventesFiltrees, txtRecherche, dpDebut, dpFin, cmbOrdonnance));
        cmbOrdonnance.valueProperty().addListener((obs, o, n) ->
            appliquerFiltres(ventesFiltrees, txtRecherche, dpDebut, dpFin, cmbOrdonnance));

        root.setTop(filtres);
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1000, 600);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void appliquerFiltres(FilteredList<Vente> ventesFiltrees,
                                  TextField txtRecherche,
                                  DatePicker dpDebut,
                                  DatePicker dpFin,
                                  ComboBox<String> cmbOrdonnance) {
        String query = txtRecherche.getText() != null ? txtRecherche.getText().trim().toLowerCase() : "";
        LocalDate debut = dpDebut.getValue();
        LocalDate fin = dpFin.getValue();
        String filtreOrdonnance = cmbOrdonnance.getValue();

        ventesFiltrees.setPredicate(vente -> {
            if (vente == null) return false;

            // Filtre date
            LocalDate dateVente = vente.getDateHeure().toLocalDate();
            if (debut != null && dateVente.isBefore(debut)) return false;
            if (fin != null && dateVente.isAfter(fin)) return false;

            // Filtre ordonnance
            if ("Avec ordonnance".equals(filtreOrdonnance) && !vente.isAvecOrdonnance()) return false;
            if ("Sans ordonnance".equals(filtreOrdonnance) && vente.isAvecOrdonnance()) return false;

            // Filtre recherche
            if (!query.isEmpty()) {
                if (String.valueOf(vente.getId()).contains(query)) return true;
                for (LigneVente ligne : vente.getLignesVente()) {
                    if (ligne.getNomMedicament() != null &&
                        ligne.getNomMedicament().toLowerCase().contains(query)) {
                        return true;
                    }
                }
                return false;
            }

            return true;
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
