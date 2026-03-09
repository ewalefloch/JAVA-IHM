package com.ubo.tp.message.ihm.common;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractListView<T> extends JPanel {

    protected JPanel listPanel;
    private JScrollPane scrollPane;
    private JLabel titleLabel;

    private JTextField searchField;
    private List<T> allItems; // Stocke la liste complète non filtrée

    public AbstractListView(String title) {
        this.allItems = new ArrayList<>();
        initComponents(title);
    }

    private void initComponents(String title) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // champ de recherche
        searchField = new JTextField();
        searchField.setToolTipText("Rechercher...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterList(); }
            @Override public void removeUpdate(DocumentEvent e) { filterList(); }
            @Override public void changedUpdate(DocumentEvent e) { filterList(); }
        });

        // Un panel conteneur pour ajouter de l'espace sous la barre de recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        add(topPanel, BorderLayout.NORTH);

        // --- Conteneur de la liste ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Met à jour la liste complète en mémoire et déclenche l'affichage.
     */
    protected void updateList(List<T> items) {
        this.allItems = (items != null) ? items : new ArrayList<>();
        filterList();
    }

    /**
     * Filtre les éléments en fonction du texte de la barre de recherche et les affiche.
     */
    //SRS-MAP-USR-008
    //SRS-MAP-CHN-002
    //SRS-MAP-MSG-005
    private void filterList() {
        listPanel.removeAll();
        String query = searchField.getText().toLowerCase().trim();

        if (allItems.isEmpty()) {
            addEmptyMessageLabel(getEmptyMessage());
        } else {
            // Filtrage
            List<T> filteredItems = allItems;
            if (!query.isEmpty()) {
                filteredItems = allItems.stream()
                        .filter(item -> matchSearch(item, query))
                        .toList();
            }

            if (filteredItems.isEmpty()) {
                addEmptyMessageLabel("Aucun résultat pour la recherche");
            } else {
                for (T item : filteredItems) {
                    JPanel cell = createCell(item);
                    cell.setMaximumSize(new Dimension(Integer.MAX_VALUE, getCellHeight()));
                    listPanel.add(cell);
                    listPanel.add(Box.createVerticalStrut(5));
                }
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void addEmptyMessageLabel(String text) {
        JLabel emptyLabel = new JLabel(text);
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        listPanel.add(emptyLabel);
    }

    protected abstract JPanel createCell(T item);
    protected abstract String getEmptyMessage();
    protected abstract int getCellHeight();
    protected abstract boolean matchSearch(T item, String query);
}