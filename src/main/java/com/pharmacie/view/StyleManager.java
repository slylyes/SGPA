package com.pharmacie.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Gestionnaire de style pour l'application (Thème Pharmacie Moderne)
 */
public class StyleManager {

    // Palette de couleurs
    public static final String PRIMARY_COLOR = "#2E7D32";      // Vert foncé (Pharmacie)
    public static final String SECONDARY_COLOR = "#C8E6C9";    // Vert clair (Fond, accent)
    public static final String ACCENT_COLOR = "#4CAF50";       // Vert vif (Boutons action)
    public static final String BACKGROUND_COLOR = "#F5F5F5";   // Gris très clair (Fond général)
    public static final String WHITE_COLOR = "#FFFFFF";        // Blanc (Cartes, Tableaux)
    public static final String DESTRUCTIVE_COLOR = "#D32F2F";  // Rouge (Suppression, Alerte critique)
    public static final String WARNING_COLOR = "#FFA000";      // Orange (Alerte moyenne)
    public static final String TEXT_COLOR = "#333333";         // Gris foncé (Texte principal)
    public static final String TEXT_SECONDARY_COLOR = "#757575"; // Gris moyen (Texte secondaire)

    // Polices
    public static final String FONT_FAMILY = "Segoe UI, Helvetica, Arial, sans-serif";
    public static final int FONT_SIZE_HEADER = 20;
    public static final int FONT_SIZE_NORMAL = 14;

    // Nouveaux styles pour les tableaux et boutons
    private static final int TABLE_ROW_HEIGHT = 40;
    private static final int TABLE_FONT_SIZE = 14;

    // Styles CSS communs
    private static final String COMMON_BUTTON_STYLE =
        "-fx-font-family: '" + FONT_FAMILY + "'; " +
        "-fx-font-size: " + FONT_SIZE_NORMAL + "px; " +
        "-fx-font-weight: bold; " +
        "-fx-background-radius: 8; " +
        "-fx-border-radius: 8; " +
        "-fx-cursor: hand; " +
        "-fx-padding: 8 16 8 16;";

    private static final String TEXT_FIELD_STYLE =
        "-fx-font-family: '" + FONT_FAMILY + "'; " +
        "-fx-font-size: " + FONT_SIZE_NORMAL + "px; " +
        "-fx-background-radius: 5; " +
        "-fx-border-radius: 5; " +
        "-fx-border-color: #BDBDBD; " +
        "-fx-border-width: 1; " +
        "-fx-padding: 5;";

    /**
     * Applique le style au conteneur principal (BorderPane ou VBox)
     */
    public static void applyBackgroundStyle(Region region) {
        region.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
    }

    /**
     * Crée et style l'en-tête de page
     */
    public static HBox createHeader(String titleText, Runnable onBack) {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label title = new Label(titleText);
        title.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_HEADER));
        title.setStyle("-fx-text-fill: " + WHITE_COLOR + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (onBack != null) {
            Button btnBack = new Button("← Retour");
            applySecondaryButtonStyle(btnBack);
            // Légère adaptation pour le header
            btnBack.setStyle(btnBack.getStyle() + "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-border-color: transparent;");

            btnBack.setOnMouseEntered(e -> btnBack.setStyle(btnBack.getStyle().replace("rgba(255,255,255,0.2)", "rgba(255,255,255,0.4)")));
            btnBack.setOnMouseExited(e -> btnBack.setStyle(btnBack.getStyle().replace("rgba(255,255,255,0.4)", "rgba(255,255,255,0.2)")));

            btnBack.setOnAction(e -> onBack.run());
            header.getChildren().addAll(btnBack, spacer, title);
        } else {
            header.getChildren().addAll(title, spacer);
        }

        // Ajout d'un spacer à droite pour équilibrer si bouton retour
        if (onBack != null) {
            Region rightSpacer = new Region();
            HBox.setHgrow(rightSpacer, Priority.ALWAYS);
            header.getChildren().add(rightSpacer);
        }

        return header;
    }

    /**
     * Applique le style pour un bouton principal (vert)
     */
    public static void applyPrimaryButtonStyle(Button button) {
        button.setStyle(COMMON_BUTTON_STYLE +
            "-fx-background-color: " + ACCENT_COLOR + "; " +
            "-fx-text-fill: white;");

        button.setOnMouseEntered(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: #43A047; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: white;"));
    }

    /**
     * Applique le style pour un bouton secondaire (blanc/gris)
     */
    public static void applySecondaryButtonStyle(Button button) {
        button.setStyle(COMMON_BUTTON_STYLE +
            "-fx-background-color: white; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-border-color: " + PRIMARY_COLOR + "; " +
            "-fx-border-width: 1;");

        button.setOnMouseEntered(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + ";"));
        button.setOnMouseExited(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: " + PRIMARY_COLOR + ";"));
    }

    /**
     * Applique le style pour un bouton destructif (rouge)
     */
    public static void applyDestructiveButtonStyle(Button button) {
        button.setStyle(COMMON_BUTTON_STYLE +
            "-fx-background-color: " + DESTRUCTIVE_COLOR + "; " +
            "-fx-text-fill: white;");

        button.setOnMouseEntered(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: #B71C1C; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: " + DESTRUCTIVE_COLOR + "; -fx-text-fill: white;"));
    }

    /**
     * Applique le style pour un bouton d'avertissement (orange)
     */
    public static void applyWarningButtonStyle(Button button) {
        button.setStyle(COMMON_BUTTON_STYLE +
            "-fx-background-color: " + WARNING_COLOR + "; " +
            "-fx-text-fill: white;");

        button.setOnMouseEntered(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: #FF8F00; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle(COMMON_BUTTON_STYLE + "-fx-background-color: " + WARNING_COLOR + "; -fx-text-fill: white;"));
    }

    /**
     * Applique le style aux champs de texte
     */
    public static void applyTextFieldStyle(TextField textField) {
        textField.setStyle(TEXT_FIELD_STYLE);
        textField.setOnMouseEntered(e -> textField.setStyle(TEXT_FIELD_STYLE + "-fx-border-color: " + PRIMARY_COLOR + ";"));
        textField.setOnMouseExited(e -> {
            if (!textField.isFocused()) textField.setStyle(TEXT_FIELD_STYLE);
        });
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) textField.setStyle(TEXT_FIELD_STYLE + "-fx-border-color: " + PRIMARY_COLOR + "; -fx-border-width: 2;");
            else textField.setStyle(TEXT_FIELD_STYLE);
        });
    }

    /**
     * Applique un style amélioré aux tables (fines, modernes)
     */
    public static void applyTableStyle(TableView<?> table) {
        // Base style
        String style =
            "-fx-background-color: white; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-family: '" + FONT_FAMILY + "'; " +
            "-fx-font-size: " + TABLE_FONT_SIZE + "px; " +
            "-fx-control-inner-background: white; " +
            "-fx-control-inner-background-alt: #F1F8E9; " + // Alternance vert très clair
            "-fx-table-cell-border-color: transparent; " +   // Supprime les lignes verticales
            "-fx-table-header-border-color: transparent; " +
            "-fx-padding: 5;";

        table.setStyle(style);

        // Configuration de la hauteur des lignes
        table.setFixedCellSize(TABLE_ROW_HEIGHT);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Placeholder moderne
        Label placeholder = new Label("Aucune donnée disponible");
        placeholder.setFont(Font.font(FONT_FAMILY, 16));
        placeholder.setStyle("-fx-text-fill: " + TEXT_SECONDARY_COLOR + ";");
        table.setPlaceholder(placeholder);
    }

    /**
     * Crée un gros bouton de menu moderne
     */
    public static Button createLargeMenuButton(String title, String description) {
        Button button = new Button();
        button.setPrefSize(300, 180);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 18));
        lblTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        lblTitle.setWrapText(true);
        lblTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label lblDesc = new Label(description);
        lblDesc.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 12));
        lblDesc.setStyle("-fx-text-fill: " + TEXT_SECONDARY_COLOR + ";");
        lblDesc.setWrapText(true);
        lblDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        content.getChildren().addAll(lblTitle, lblDesc);
        button.setGraphic(content);

        // Style du bouton
        String baseStyle =
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-border-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); " +
            "-fx-cursor: hand;";

        button.setStyle(baseStyle);

        // Animation hover
        button.setOnMouseEntered(e -> {
            button.setStyle(baseStyle + "-fx-background-color: " + SECONDARY_COLOR + "; " +
                          "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            lblTitle.setStyle("-fx-text-fill: #1B5E20;"); // Vert plus foncé au survol
        });

        button.setOnMouseExited(e -> {
            button.setStyle(baseStyle);
            lblTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        });

        return button;
    }

    /**
     * Crée un conteneur style "Carte" (fond blanc, ombre légère)
     */
    public static VBox createCard(String title) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + WHITE_COLOR + "; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
        );

        if (title != null && !title.isEmpty()) {
            Label lblTitle = new Label(title);
            lblTitle.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16));
            lblTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
            card.getChildren().add(lblTitle);
            card.getChildren().add(new Separator());
        }

        return card;
    }

    /**
     * Crée un label de sous-titre standard
     */
    public static Label createSubtitleLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE_NORMAL));
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY_COLOR + ";");
        return label;
    }
}
