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
 * Vue pour les alertes de stock et de péremption
 */
public class AlerteView {
    private Stage stage;
    private MedicamentController controller;

    public AlerteView(Stage stage) {
        this.stage = stage;
        this.controller = new MedicamentController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        StyleManager.applyBackgroundStyle(root);

        // En-tête
        HBox header = StyleManager.createHeader("Alertes Stock et Péremption", () -> {
            MainMenuView mainMenu = new MainMenuView(stage);
            mainMenu.show();
        });
        root.setTop(header);

        // Contenu
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createAlertesStockTab(), createAlertesPeremptionTab());
        tabPane.setStyle("-fx-tab-min-width: 150px; -fx-font-family: '" + StyleManager.FONT_FAMILY + "';");
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Alertes - SGPA");
        stage.show();
    }

    private Tab createAlertesStockTab() {
        Tab tab = new Tab("Alertes Stock Minimum");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label info = StyleManager.createSubtitleLabel("Médicaments dont le stock est en dessous du seuil minimum");

        TableView<Medicament> table = new TableView<>();
        StyleManager.applyTableStyle(table);

        ObservableList<Medicament> data = FXCollections.observableArrayList(
            controller.getMedicamentsEnAlerteStock()
        );
        table.setItems(data);

        TableColumn<Medicament, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Medicament, String> colNom = new TableColumn<>("Nom Commercial");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        colNom.setPrefWidth(300);

        TableColumn<Medicament, Integer> colStock = new TableColumn<>("Stock Actuel");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActuel"));
        colStock.setPrefWidth(120);
        colStock.setStyle("-fx-alignment: CENTER;");

        TableColumn<Medicament, Integer> colSeuil = new TableColumn<>("Seuil Minimum");
        colSeuil.setCellValueFactory(new PropertyValueFactory<>("seuilMinimum"));
        colSeuil.setPrefWidth(120);
        colSeuil.setStyle("-fx-alignment: CENTER;");

        TableColumn<Medicament, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> {
            Medicament med = cellData.getValue();
            String statut = med.getStockActuel() == 0 ? "RUPTURE" : "ALERTE";
            return new javafx.beans.property.SimpleStringProperty(statut);
        });
        colStatut.setPrefWidth(120);

        colStatut.setCellFactory(tc -> new TableCell<Medicament, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("RUPTURE")) {
                         setStyle("-fx-text-fill: " + StyleManager.DESTRUCTIVE_COLOR + "; -fx-font-weight: bold;");
                    } else {
                         setStyle("-fx-text-fill: " + StyleManager.WARNING_COLOR + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        table.getColumns().addAll(colId, colNom, colStock, colSeuil, colStatut);

        // Colorier les lignes selon le niveau d'alerte
        table.setRowFactory(tv -> new TableRow<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getStockActuel() == 0) {
                    setStyle("-fx-background-color: #ffebee;");  // Very light red
                } else {
                    setStyle("-fx-background-color: #fff8e1;");  // Very light orange
                }
            }
        });

        Label lblTotal = new Label("Total: " + data.size() + " médicament(s) en alerte");
        lblTotal.setFont(javafx.scene.text.Font.font(StyleManager.FONT_FAMILY, javafx.scene.text.FontWeight.BOLD, 14));
        lblTotal.setStyle("-fx-text-fill: " + StyleManager.TEXT_COLOR + ";");

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> {
            data.clear();
            data.addAll(controller.getMedicamentsEnAlerteStock());
            lblTotal.setText("Total: " + data.size() + " médicament(s) en alerte");
        });

        HBox footer = new HBox(10, lblTotal, new Region(), btnActualiser);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        content.getChildren().addAll(info, table, footer);
        VBox.setVgrow(table, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private Tab createAlertesPeremptionTab() {
        Tab tab = new Tab("Alertes Péremption");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label info = StyleManager.createSubtitleLabel("Médicaments dont la date de péremption est dans moins de 3 mois");

        TableView<Medicament> table = new TableView<>();
        StyleManager.applyTableStyle(table);

        ObservableList<Medicament> data = FXCollections.observableArrayList(
            controller.getMedicamentsProchesPeremption()
        );
        table.setItems(data);

        TableColumn<Medicament, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Medicament, String> colNom = new TableColumn<>("Nom Commercial");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        colNom.setPrefWidth(250);

        TableColumn<Medicament, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActuel"));
        colStock.setPrefWidth(100);

        TableColumn<Medicament, LocalDate> colPeremption = new TableColumn<>("Date Péremption");
        colPeremption.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));
        colPeremption.setPrefWidth(150);

        TableColumn<Medicament, String> colJoursRestants = new TableColumn<>("Jours Restants");
        colJoursRestants.setCellValueFactory(cellData -> {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), 
                cellData.getValue().getDatePeremption()
            );
            return new javafx.beans.property.SimpleStringProperty(jours + " jours");
        });
        colJoursRestants.setPrefWidth(150);

        table.getColumns().addAll(colId, colNom, colStock, colPeremption, colJoursRestants);

        // Colorier les lignes selon l'urgence
        table.setRowFactory(tv -> new TableRow<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    long jours = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(), 
                        item.getDatePeremption()
                    );
                    if (jours < 0) {
                        setStyle("-fx-background-color: #ef9a9a;");  // Red
                    } else if (jours < 30) {
                        setStyle("-fx-background-color: #ffebee;");  // Light Red
                    } else {
                        setStyle("-fx-background-color: #fff8e1;");  // Light Orange
                    }
                }
            }
        });

        Label lblTotal = new Label("Total: " + data.size() + " médicament(s) proche de la péremption");
        lblTotal.setFont(javafx.scene.text.Font.font(StyleManager.FONT_FAMILY, javafx.scene.text.FontWeight.BOLD, 14));
        lblTotal.setStyle("-fx-text-fill: " + StyleManager.TEXT_COLOR + ";");

        Button btnActualiser = new Button("Actualiser");
        StyleManager.applySecondaryButtonStyle(btnActualiser);
        btnActualiser.setOnAction(e -> {
            data.clear();
            data.addAll(controller.getMedicamentsProchesPeremption());
            lblTotal.setText("Total: " + data.size() + " médicament(s) proche de la péremption");
        });

        HBox footer = new HBox(10, lblTotal, new Region(), btnActualiser);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        content.getChildren().addAll(info, table, footer);
        VBox.setVgrow(table, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }
}
