package com.ubo.tp.message.ihm.common.fx;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;

public abstract class AbstractCellViewFx<T> extends BorderPane {

    protected final T item;
    protected boolean selected = false;

    protected AbstractCellViewFx(T item) {
        this.item = item;
        initBaseComponents();
    }

    private void initBaseComponents() {
        this.setPadding(new Insets(8, 10, 8, 10));
        this.setCursor(Cursor.HAND);
        setUnselectedStyle();

        // Ajout du contenu spécifique de l'enfant
        buildContent();

        // Gestion du survol
        this.setOnMouseEntered(evt -> {
            if (!selected) {
                this.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: lightgray; -fx-border-width: 1;");
            }
        });

        this.setOnMouseExited(evt -> {
            if (!selected) {
                setUnselectedStyle();
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setSelectedStyle();
        } else {
            setUnselectedStyle();
        }
    }

    private void setUnselectedStyle() {
        this.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 1;");
    }

    private void setSelectedStyle() {
        // Couleurs similaires à ta version Swing
        this.setStyle("-fx-background-color: #DCE6FF; -fx-border-color: #5865F2; -fx-border-width: 2;");
    }

    public T getItem() {
        return item;
    }

    public boolean isSelected() {
        return selected;
    }

    protected abstract void buildContent();
    public abstract void refresh();
}