package com.pharmacie.view;

import com.pharmacie.controller.RapportController;
import com.pharmacie.model.RapportVente;
import com.pharmacie.model.TopMedicament;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vue pour la consultation et l'export des rapports financiers
 */
public class RapportView {
    private Stage stage;
    private RapportController rapportController;

    // Composants de filtrage
    private DatePicker dpDebut;
    private DatePicker dpFin;
    private Spinner<Integer> spinTopN;

    // Tableaux de données
    private TableView<RapportVente> tableVentes;
    private TableView<TopMedicament> tableTop;

    // Indicateurs
    private Label lblCA;
    private Label lblNbVentes;
    private Label lblPanierMoyen;

    // Données courantes
    private List<RapportVente> ventesParMois;
    private List<TopMedicament> topMedicaments;
    private double caTotalPeriode;
    private int nbVentesPeriode;

    public RapportView(Stage stage) {
        this.stage = stage;
        this.rapportController = new RapportController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Rapports Financiers", () -> {
            new MainMenuView(stage).show();
        });

        // Boutons d'export dans le header
        HBox exportBox = new HBox(10);
        exportBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnExportExcel = new Button("📊 Export Excel");
        StyleManager.applySecondaryButtonStyle(btnExportExcel);
        btnExportExcel.setStyle(btnExportExcel.getStyle() +
                "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-color: transparent;");
        btnExportExcel.setOnAction(e -> exporterExcel());

        Button btnExportPdf = new Button("📄 Export PDF");
        StyleManager.applySecondaryButtonStyle(btnExportPdf);
        btnExportPdf.setStyle(btnExportPdf.getStyle() +
                "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-color: transparent;");
        btnExportPdf.setOnAction(e -> exporterPdf());

        exportBox.getChildren().addAll(btnExportExcel, btnExportPdf);
        header.getChildren().add(exportBox);

        root.setTop(header);

        // Contenu principal dans un ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox contenu = new VBox(20);
        contenu.setPadding(new Insets(20));

        contenu.getChildren().addAll(
                creerBarreFiltres(),
                creerCarteIndicateurs(),
                creerTableauVentes(),
                creerTableauTopMedicaments()
        );

        scrollPane.setContent(contenu);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Rapports Financiers - SGPA");
        stage.show();

        // Charger les données initiales (année en cours, du 1er janvier à aujourd'hui)
        dpDebut.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        dpFin.setValue(LocalDate.now());
        actualiserDonnees();
    }

    // ===================== BARRE DE FILTRES =====================

    private HBox creerBarreFiltres() {
        HBox barre = new HBox(15);
        barre.setPadding(new Insets(15));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
        );

        Label lblPeriode = new Label("Période :");
        lblPeriode.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 14));
        lblPeriode.setStyle("-fx-text-fill: " + StyleManager.TEXT_COLOR + ";");

        dpDebut = new DatePicker();
        dpDebut.setPromptText("Date début");
        dpDebut.setPrefWidth(150);

        Label lblA = new Label("→");
        lblA.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 16));
        lblA.setStyle("-fx-text-fill: " + StyleManager.PRIMARY_COLOR + ";");

        dpFin = new DatePicker();
        dpFin.setPromptText("Date fin");
        dpFin.setPrefWidth(150);

        // Séparateur
        Region sep = new Region();
        sep.setPrefWidth(20);

        Label lblTop = new Label("Top N :");
        lblTop.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 14));
        lblTop.setStyle("-fx-text-fill: " + StyleManager.TEXT_COLOR + ";");

        spinTopN = new Spinner<>(3, 50, 10, 1);
        spinTopN.setPrefWidth(80);
        spinTopN.setEditable(true);

        // Raccourcis de période
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnMois = creerBoutonRaccourci("Ce mois");
        btnMois.setOnAction(e -> {
            dpDebut.setValue(LocalDate.now().withDayOfMonth(1));
            dpFin.setValue(LocalDate.now());
            actualiserDonnees();
        });

        Button btnTrimestre = creerBoutonRaccourci("Trimestre");
        btnTrimestre.setOnAction(e -> {
            int mois = LocalDate.now().getMonthValue();
            int debutTrimestre = ((mois - 1) / 3) * 3 + 1;
            dpDebut.setValue(LocalDate.of(LocalDate.now().getYear(), debutTrimestre, 1));
            dpFin.setValue(LocalDate.now());
            actualiserDonnees();
        });

        Button btnAnnee = creerBoutonRaccourci("Cette année");
        btnAnnee.setOnAction(e -> {
            dpDebut.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
            dpFin.setValue(LocalDate.now());
            actualiserDonnees();
        });

        Button btnActualiser = new Button("🔄 Actualiser");
        StyleManager.applyPrimaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> actualiserDonnees());

        barre.getChildren().addAll(
                lblPeriode, dpDebut, lblA, dpFin,
                sep, lblTop, spinTopN,
                spacer, btnMois, btnTrimestre, btnAnnee, btnActualiser
        );

        return barre;
    }

    private Button creerBoutonRaccourci(String texte) {
        Button btn = new Button(texte);
        StyleManager.applySecondaryButtonStyle(btn);
        btn.setStyle(btn.getStyle() + "-fx-font-size: 12px; -fx-padding: 5 10 5 10;");
        return btn;
    }

    // ===================== INDICATEURS CLÉS =====================

    private HBox creerCarteIndicateurs() {
        HBox cartes = new HBox(20);
        cartes.setAlignment(Pos.CENTER);

        lblCA = new Label("0,00 €");
        VBox carteCA = creerCarteKPI("Chiffre d'affaires", lblCA);

        lblNbVentes = new Label("0");
        VBox carteNbVentes = creerCarteKPI("Nombre de ventes", lblNbVentes);

        lblPanierMoyen = new Label("0,00 €");
        VBox cartePanier = creerCarteKPI("Panier moyen", lblPanierMoyen);

        cartes.getChildren().addAll(carteCA, carteNbVentes, cartePanier);
        HBox.setHgrow(carteCA, Priority.ALWAYS);
        HBox.setHgrow(carteNbVentes, Priority.ALWAYS);
        HBox.setHgrow(cartePanier, Priority.ALWAYS);

        return cartes;
    }

    private VBox creerCarteKPI(String titre, Label lblValeur) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(20));
        carte.setAlignment(Pos.CENTER);
        carte.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: " + StyleManager.SECONDARY_COLOR + "; -fx-border-radius: 10; " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
        );

        Label lblTitre = new Label(titre);
        lblTitre.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.NORMAL, 14));
        lblTitre.setStyle("-fx-text-fill: " + StyleManager.TEXT_SECONDARY_COLOR + ";");

        lblValeur.setFont(Font.font(StyleManager.FONT_FAMILY, FontWeight.BOLD, 28));
        lblValeur.setStyle("-fx-text-fill: " + StyleManager.PRIMARY_COLOR + ";");

        carte.getChildren().addAll(lblTitre, lblValeur);
        return carte;
    }

    // ===================== TABLEAU VENTES PAR MOIS =====================

    @SuppressWarnings("unchecked")
    private VBox creerTableauVentes() {
        VBox card = StyleManager.createCard("Ventes par mois");

        tableVentes = new TableView<>();
        StyleManager.applyTableStyle(tableVentes);
        tableVentes.setPrefHeight(300);

        TableColumn<RapportVente, String> colPeriode = new TableColumn<>("Période");
        colPeriode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPeriode()));
        colPeriode.setPrefWidth(200);

        TableColumn<RapportVente, Integer> colNbVentes = new TableColumn<>("Nb Ventes");
        colNbVentes.setCellValueFactory(new PropertyValueFactory<>("nombreVentes"));
        colNbVentes.setStyle("-fx-alignment: CENTER;");

        TableColumn<RapportVente, Integer> colQuantite = new TableColumn<>("Qté vendue");
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteTotale"));
        colQuantite.setStyle("-fx-alignment: CENTER;");

        TableColumn<RapportVente, String> colCA = new TableColumn<>("Chiffre d'affaires");
        colCA.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f €", data.getValue().getChiffreAffaires())));
        colCA.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCA.setPrefWidth(180);

        tableVentes.getColumns().addAll(colPeriode, colNbVentes, colQuantite, colCA);
        card.getChildren().add(tableVentes);

        return card;
    }

    // ===================== TABLEAU TOP MÉDICAMENTS =====================

    @SuppressWarnings("unchecked")
    private VBox creerTableauTopMedicaments() {
        VBox card = StyleManager.createCard("Top médicaments les plus vendus");

        tableTop = new TableView<>();
        StyleManager.applyTableStyle(tableTop);
        tableTop.setPrefHeight(300);

        TableColumn<TopMedicament, Integer> colRang = new TableColumn<>("#");
        colRang.setCellValueFactory(data -> {
            int index = tableTop.getItems().indexOf(data.getValue()) + 1;
            return new SimpleIntegerProperty(index).asObject();
        });
        colRang.setPrefWidth(50);
        colRang.setStyle("-fx-alignment: CENTER;");

        TableColumn<TopMedicament, String> colNom = new TableColumn<>("Médicament");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        colNom.setPrefWidth(250);

        TableColumn<TopMedicament, Integer> colQte = new TableColumn<>("Qté vendue");
        colQte.setCellValueFactory(new PropertyValueFactory<>("quantiteTotaleVendue"));
        colQte.setStyle("-fx-alignment: CENTER;");

        TableColumn<TopMedicament, Integer> colNbVentes = new TableColumn<>("Nb Ventes");
        colNbVentes.setCellValueFactory(new PropertyValueFactory<>("nombreVentes"));
        colNbVentes.setStyle("-fx-alignment: CENTER;");

        TableColumn<TopMedicament, String> colCA = new TableColumn<>("CA (€)");
        colCA.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f €", data.getValue().getChiffreAffaires())));
        colCA.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCA.setPrefWidth(150);

        tableTop.getColumns().addAll(colRang, colNom, colQte, colNbVentes, colCA);
        card.getChildren().add(tableTop);

        return card;
    }

    // ===================== CHARGEMENT DES DONNÉES =====================

    private void actualiserDonnees() {
        LocalDate debut = dpDebut.getValue();
        LocalDate fin = dpFin.getValue();

        if (debut == null || fin == null) {
            showAlert(Alert.AlertType.WARNING, "Période incomplète",
                    "Veuillez sélectionner les dates de début et de fin.");
            return;
        }

        if (debut.isAfter(fin)) {
            showAlert(Alert.AlertType.WARNING, "Période invalide",
                    "La date de début doit être antérieure à la date de fin.");
            return;
        }

        int topN = spinTopN.getValue();

        // Charger les données depuis le contrôleur
        ventesParMois = rapportController.getVentesParMoisEntreDates(debut, fin);
        topMedicaments = rapportController.getTopMedicaments(topN, debut, fin);
        caTotalPeriode = rapportController.getChiffreAffairesPeriode(debut, fin);
        nbVentesPeriode = rapportController.getNombreVentesPeriode(debut, fin);

        // Mettre à jour les indicateurs
        lblCA.setText(String.format("%.2f €", caTotalPeriode));
        lblNbVentes.setText(String.valueOf(nbVentesPeriode));
        double panierMoyen = nbVentesPeriode > 0 ? caTotalPeriode / nbVentesPeriode : 0;
        lblPanierMoyen.setText(String.format("%.2f €", panierMoyen));

        // Mettre à jour les tableaux
        tableVentes.setItems(FXCollections.observableArrayList(ventesParMois));
        tableTop.setItems(FXCollections.observableArrayList(topMedicaments));
    }

    // ===================== EXPORT =====================

    private void exporterExcel() {
        if (ventesParMois == null || ventesParMois.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune donnée",
                    "Veuillez d'abord charger des données avant d'exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport Excel");
        fileChooser.setInitialFileName("rapport_pharmacie_" +
                dpDebut.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" +
                dpFin.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier Excel (*.xlsx)", "*.xlsx"));

        File fichier = fileChooser.showSaveDialog(stage);
        if (fichier != null) {
            File result = rapportController.exporterExcel(ventesParMois, topMedicaments,
                    caTotalPeriode, nbVentesPeriode, dpDebut.getValue(), dpFin.getValue(), fichier);

            if (result != null) {
                showAlert(Alert.AlertType.INFORMATION, "Export réussi",
                        "Le rapport Excel a été enregistré :\n" + result.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur d'export",
                        "Une erreur est survenue lors de l'export Excel.");
            }
        }
    }

    private void exporterPdf() {
        if (ventesParMois == null || ventesParMois.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune donnée",
                    "Veuillez d'abord charger des données avant d'exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.setInitialFileName("rapport_pharmacie_" +
                dpDebut.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" +
                dpFin.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier PDF (*.pdf)", "*.pdf"));

        File fichier = fileChooser.showSaveDialog(stage);
        if (fichier != null) {
            File result = rapportController.exporterPdf(ventesParMois, topMedicaments,
                    caTotalPeriode, nbVentesPeriode, dpDebut.getValue(), dpFin.getValue(), fichier);

            if (result != null) {
                showAlert(Alert.AlertType.INFORMATION, "Export réussi",
                        "Le rapport PDF a été enregistré :\n" + result.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur d'export",
                        "Une erreur est survenue lors de l'export PDF.");
            }
        }
    }

    // ===================== UTILITAIRES =====================

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
