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
        root.setStyle("-fx-background-color: #f5f5f5;");

        // En-tête
        HBox header = createHeader();
        root.setTop(header);

        // Contenu
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createAlertesStockTab(), createAlertesPeremptionTab());
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #FF9800;");

        Label titre = new Label("Alertes Stock et Péremption");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: white; -fx-text-fill: #FF9800;");
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

    private Tab createAlertesStockTab() {
        Tab tab = new Tab("Alertes Stock Minimum");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label info = new Label("Médicaments dont le stock est en dessous du seuil minimum");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        TableView<Medicament> table = new TableView<>();
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

        table.getColumns().addAll(colId, colNom, colStock, colSeuil, colStatut);

        // Colorier les lignes selon le niveau d'alerte
        table.setRowFactory(tv -> new TableRow<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getStockActuel() == 0) {
                    setStyle("-fx-background-color: #ffcdd2;");  // Rouge clair
                } else {
                    setStyle("-fx-background-color: #fff3e0;");  // Orange clair
                }
            }
        });

        Label lblTotal = new Label("Total: " + data.size() + " médicament(s) en alerte");
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button btnActualiser = new Button("Actualiser");
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

        Label info = new Label("Médicaments dont la date de péremption est dans moins de 3 mois");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        TableView<Medicament> table = new TableView<>();
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
                        setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");  // Périmé
                    } else if (jours < 30) {
                        setStyle("-fx-background-color: #ffcdd2;");  // Moins d'un mois
                    } else {
                        setStyle("-fx-background-color: #fff3e0;");  // Moins de 3 mois
                    }
                }
            }
        });

        Label lblTotal = new Label("Total: " + data.size() + " médicament(s) proche de la péremption");
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button btnActualiser = new Button("Actualiser");
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
