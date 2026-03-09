package main.java.com.ubo.tp.message.ihm.channel.fx;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.com.ubo.tp.message.controller.ChannelListController;
import main.java.com.ubo.tp.message.controller.observer.IChannelListObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.fx.AbstractListViewFx;

import javax.swing.*;
import java.util.List;

public class ChannelListViewFx extends AbstractListViewFx<Channel> implements IChannelListObserver {

    private final ChannelListController controller;
    private ChannelCellViewFx selectedChannelCell;

    public ChannelListViewFx(ChannelListController controller) {
        super("Canaux");
        this.controller = controller;
        addCreateButton();
        this.controller.addObserver(this);
    }

    private void addCreateButton() {
        Button createBtn = new Button("+");
        createBtn.setStyle("-fx-background-color: #43B581; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        createBtn.setOnAction(e -> showCreateChannelDialog());

        VBox headerPanel = (VBox) this.getTop();

        TextField searchField = (TextField) headerPanel.getChildren().get(1);

        headerPanel.getChildren().remove(searchField);

        HBox searchContainer = new HBox(5);
        searchContainer.getChildren().addAll(searchField, createBtn);

        HBox.setHgrow(searchField, javafx.scene.layout.Priority.ALWAYS);

        headerPanel.getChildren().add(searchContainer);
    }

    @Override
    protected BorderPane createCell(Channel item) {
        ChannelCellViewFx cell = new ChannelCellViewFx(item, controller);

        cell.setOnMouseClicked(e -> {
            if (selectedChannelCell != null) {
                selectedChannelCell.setSelected(false);
            }
            selectedChannelCell = cell;
            cell.setSelected(true);

            controller.selectChannel(item);
        });

        return cell;
    }

    @Override
    protected String getEmptyMessage() {
        return "Aucun canal disponible";
    }

    @Override
    protected int getCellHeight() {
        return 70;
    }

    public void cleanup() {
        controller.removeObserver(this);
    }

    @Override
    public void onChannelListChanged() {
        Platform.runLater(() -> updateList(controller.getChannels()));
    }

    @Override
    protected boolean matchSearch(Channel item, String query) {
        return item.getName() != null && item.getName().toLowerCase().contains(query);
    }

    private void showCreateChannelDialog() {
        Dialog<Channel> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Canal");
        dialog.setHeaderText("Créer un nouveau canal de discussion");

        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(350, 400);

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Composants
        TextField nameField = new TextField();
        nameField.setPromptText("Nom du canal");

        ToggleGroup group = new ToggleGroup();
        RadioButton publicRadio = new RadioButton("Public");
        publicRadio.setToggleGroup(group);
        publicRadio.setSelected(true);
        RadioButton privateRadio = new RadioButton("Privé");
        privateRadio.setToggleGroup(group);

        HBox radioBox = new HBox(10, publicRadio, privateRadio);

        // Liste des utilisateurs
        ListView<User> userListView = new ListView<>();
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("@" + item.getUserTag() + " (" + item.getName() + ")");
                }
            }
        });
        userListView.getItems().addAll(controller.getAllUsers());

        userListView.setVisible(false);
        userListView.setManaged(false);

        javafx.scene.layout.VBox.setVgrow(userListView, javafx.scene.layout.Priority.ALWAYS);

        privateRadio.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            userListView.setVisible(isNowSelected);
            userListView.setManaged(isNowSelected);
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        VBox content = new VBox(10, new Label("Nom :"), nameField, new Label("Visibilité :"), radioBox, userListView, errorLabel);
        content.setPadding(new javafx.geometry.Insets(10));
        dialog.getDialogPane().setContent(content);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {

            String name = nameField.getText();
            boolean isPrivate = privateRadio.isSelected();
            List<User> selectedUsers = userListView.getSelectionModel().getSelectedItems();

            String errorMessage = controller.createChannel(name, isPrivate, selectedUsers);

            if (errorMessage != null) {
                errorLabel.setText(errorMessage);
                event.consume();
            }
        });

        dialog.showAndWait();
    }
}