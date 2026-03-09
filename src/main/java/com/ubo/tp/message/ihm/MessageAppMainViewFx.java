package com.ubo.tp.message.ihm;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import com.ubo.tp.message.controller.MenuActionController;

import java.util.Optional;

public class MessageAppMainViewFx extends Stage {

    private final BorderPane root;
    private final MenuActionController menuActionController;

    public MessageAppMainViewFx(MenuActionController controller) {
        this.menuActionController = controller;
        this.root = new BorderPane();
        initGui();
        initMenu();
    }

    private void initGui() {
        this.setTitle("MessageApp");
        this.setWidth(800);
        this.setHeight(800);

        try {
            Image icon = new Image("file:src/main/resources/images/logo_20.png");
            this.getIcons().add(icon);
        } catch (Exception e) {
            // ignore
        }

        // Fermer proprement la session à la croix de la fenêtre
        this.setOnCloseRequest(e -> handleQuit());

        Scene scene = new Scene(root);
        this.setScene(scene);
        this.centerOnScreen();
    }

    private void initMenu() {
        MenuBar menuBar = new MenuBar();

        // === Menu Fichier ===
        Menu menuFichier = new Menu("Fichier");

        MenuItem decoItem = new MenuItem("Déconnexion");
        decoItem.setOnAction(e -> handleLogout());

        MenuItem updateTagItem = new MenuItem("Modifier nom");
        updateTagItem.setStyle("-fx-text-fill: green;");
        updateTagItem.setOnAction(e -> handleModifyTag());

        MenuItem deleteItem = new MenuItem("Supprimer le compte");
        deleteItem.setStyle("-fx-text-fill: red;");
        deleteItem.setOnAction(e -> handleDeleteAccount());

        MenuItem quitItem = new MenuItem("Quitter");
        quitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        quitItem.setOnAction(e -> handleQuit());

        menuFichier.getItems().addAll(
                decoItem, new SeparatorMenuItem(),
                updateTagItem, new SeparatorMenuItem(),
                deleteItem, new SeparatorMenuItem(),
                quitItem
        );

        // === Menu Aide ===
        Menu helpMenu = new Menu("?");

        MenuItem aboutItem = new MenuItem("À propos");
        aboutItem.setAccelerator(KeyCombination.keyCombination("F1"));
        aboutItem.setOnAction(e -> showAboutDialog());

        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(menuFichier, helpMenu);
        root.setTop(menuBar); // Ajout du menu en haut du BorderPane
    }

    private void handleModifyTag() {
        // Équivalent du JOptionPane.showInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modification du nom");
        dialog.setHeaderText("Entrez votre nouveau nom d'utilisateur :");
        dialog.setContentText("Nom :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newTag -> {
            if (!newTag.trim().isEmpty()) {
                menuActionController.modifyUserTag(newTag.trim());
            }
        });
    }

    private void handleDeleteAccount() {
        // Équivalent du JOptionPane.showConfirmDialog
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            menuActionController.deleteAccount();
        }
    }

    private void handleLogout() {
        menuActionController.logout();
    }

    private void handleQuit() {
        menuActionController.exit();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("MessageApp\nUBO M2-TIIL\nDépartement Informatique");
        alert.setContentText("Application de messagerie");

        try {
            ImageView icon = new ImageView(new Image("file:src/main/resources/images/logo_50.png"));
            alert.setGraphic(icon);
        } catch (Exception e) {
            // ignore
        }

        alert.showAndWait();
    }

    public void setContentPane(Region view) {
        root.setCenter(view);
    }
}