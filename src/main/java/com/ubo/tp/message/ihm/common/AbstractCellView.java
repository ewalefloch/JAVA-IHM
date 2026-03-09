package com.ubo.tp.message.ihm.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class AbstractCellView<T> extends JPanel {

    protected final T item;
    protected boolean selected = false;

    protected AbstractCellView(T item) {
        this.item = item;
        initBaseComponents();
    }

    private void initBaseComponents() {
        setLayout(new BorderLayout(10, 5));
        setUnselectedStyle();
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajout du contenu spécifique de l'enfant
        buildContent();

        // Gestion du survol
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (!selected) {
                    setBackground(new Color(245, 245, 245));
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (!selected) {
                    setBackground(Color.WHITE);
                }
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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void setSelectedStyle() {
        setBackground(new Color(220, 230, 255));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(88, 101, 242), 2),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
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