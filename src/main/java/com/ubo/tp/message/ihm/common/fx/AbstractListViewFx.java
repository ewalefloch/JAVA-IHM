package main.java.com.ubo.tp.message.ihm.common.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractListViewFx<T> extends BorderPane {

    protected VBox listPanel;
    private TextField searchField;
    private List<T> allItems;

    public AbstractListViewFx(String title) {
        this.allItems = new ArrayList<>();
        initComponents(title);
    }

    private void initComponents(String title) {
        this.setPadding(new Insets(10));

        // En-tête
        VBox headerPanel = new VBox(10); // Espacement de 10px

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        // Équivalent du DocumentListener de Swing
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterList());

        headerPanel.getChildren().addAll(titleLabel, searchField);
        headerPanel.setPadding(new Insets(0, 0, 10, 0));

        this.setTop(headerPanel);

        // Conteneur de la liste (Équivalent de ton BoxLayout Y_AXIS)
        listPanel = new VBox(5); // 5px d'espacement entre les cellules
        listPanel.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(listPanel);
        scrollPane.setStyle("-fx-border-color: lightgray; -fx-background-color: white;");
        scrollPane.setFitToWidth(true); // Pour que les cellules prennent toute la largeur
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        this.setCenter(scrollPane);
    }

    protected void updateList(List<T> items) {
        this.allItems = (items != null) ? items : new ArrayList<>();
        filterList();
    }

    private void filterList() {
        listPanel.getChildren().clear();
        String query = searchField.getText().toLowerCase().trim();

        if (allItems.isEmpty()) {
            addEmptyMessageLabel(getEmptyMessage());
        } else {
            List<T> filteredItems = allItems;
            if (!query.isEmpty()) {
                filteredItems = allItems.stream()
                        .filter(item -> matchSearch(item, query))
                        .collect(Collectors.toList());
            }

            if (filteredItems.isEmpty()) {
                addEmptyMessageLabel("Aucun résultat pour la recherche");
            } else {
                for (T item : filteredItems) {
                    BorderPane cell = createCell(item);
                    // Force la hauteur de la cellule
                    cell.setMinHeight(getCellHeight());
                    cell.setMaxHeight(getCellHeight());
                    listPanel.getChildren().add(cell);
                }
            }
        }
    }

    private void addEmptyMessageLabel(String text) {
        Label emptyLabel = new Label(text);
        emptyLabel.setTextFill(Color.GRAY);
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);
        emptyLabel.setPadding(new Insets(20, 0, 20, 0));
        listPanel.getChildren().add(emptyLabel);
    }

    protected abstract BorderPane createCell(T item);
    protected abstract String getEmptyMessage();
    protected abstract int getCellHeight();
    protected abstract boolean matchSearch(T item, String query);
}