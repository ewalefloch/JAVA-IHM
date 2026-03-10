package com.ubo.tp.message.ihm.user.fx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.ubo.tp.message.controller.UserListController;
import com.ubo.tp.message.controller.observer.IUserListObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.common.fx.AbstractListViewFx;

import java.util.ArrayList;
import java.util.List;

public class UserListViewFx extends AbstractListViewFx<User> implements IUserListObserver {

    private final UserListController controller;
    private UserCellViewFx selectedUserCell;
    private Button manageBtn;
    private Button leaveBtn;

    public UserListViewFx(UserListController controller) {
        super("Utilisateurs");
        this.controller = controller;
        addHeaderButtons();
        this.controller.addObserver(this);
    }

    private void addHeaderButtons() {
        manageBtn = new Button("Gérer canal");
        manageBtn.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        manageBtn.setVisible(false);
        manageBtn.setManaged(false);
        manageBtn.setOnAction(e -> showManageChannelDialog());

        leaveBtn = new Button("Quitter canal");
        leaveBtn.setStyle("-fx-background-color: #ED4245; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        leaveBtn.setVisible(false);
        leaveBtn.setManaged(false);
        leaveBtn.setOnAction(e -> controller.leaveCurrentChannel()); // Appel au contrôleur

        VBox headerPanel = (VBox) this.getTop();
        TextField searchField = (TextField) headerPanel.getChildren().get(1);

        headerPanel.getChildren().remove(searchField);

        HBox searchContainer = new HBox(5);
        searchContainer.getChildren().addAll(searchField, manageBtn, leaveBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        headerPanel.getChildren().add(searchContainer);
    }
    @Override
    protected BorderPane createCell(User item) {
        boolean isMyself = item.getUuid().equals(controller.getConnectedUser().getUuid());

        UserCellViewFx cell = new UserCellViewFx(item, isMyself, () -> {
            controller.selectUserForMessaging(item);
        });

        cell.setOnMouseClicked(e -> selectUser(cell));

        return cell;
    }

    private void selectUser(UserCellViewFx userCell) {
        if (selectedUserCell != null) {
            selectedUserCell.setSelected(false);
        }
        selectedUserCell = userCell;
        userCell.setSelected(true);
    }

    @Override
    protected String getEmptyMessage() {
        return "Aucun utilisateur";
    }

    @Override
    protected int getCellHeight() {
        return 60;
    }

    @Override
    public void onUserListChanged() {
        List<User> users = controller.getUsers();
        Platform.runLater(() -> {
            updateList(users);

            if (manageBtn != null && leaveBtn != null) {
                boolean canManage = controller.canManageCurrentChannel();
                boolean canLeave = controller.canLeaveCurrentChannel();

                manageBtn.setVisible(canManage);
                manageBtn.setManaged(canManage);

                leaveBtn.setVisible(canLeave);
                leaveBtn.setManaged(canLeave);
            }
        });
    }

    @Override
    protected boolean matchSearch(User item, String query) {
        boolean matchName = item.getName() != null && item.getName().toLowerCase().contains(query);
        boolean matchTag = item.getUserTag() != null && item.getUserTag().toLowerCase().contains(query);
        return matchName || matchTag;
    }

    private void showManageChannelDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Gérer les membres du canal");
        dialog.setHeaderText("Ajouter ou exclure des utilisateurs de votre canal privé");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        ListView<User> outChannelList = new ListView<>();
        outChannelList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outChannelList.getItems().addAll(controller.getUsersNotInCurrentChannel());
        outChannelList.setPrefSize(200, 250);

        ListView<User> inChannelList = new ListView<>();
        inChannelList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inChannelList.getItems().addAll(controller.getUsersInCurrentChannel());
        inChannelList.setPrefSize(200, 250);

        Callback<ListView<User>, ListCell<User>> cellFactory = param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("@" + item.getUserTag() + " (" + item.getName() + ")");
                }
            }
        };
        outChannelList.setCellFactory(cellFactory);
        inChannelList.setCellFactory(cellFactory);

        Button addBtn = new Button("Ajouter >>");
        Button removeBtn = new Button("<< Retirer");

        addBtn.setOnAction(e -> {
            List<User> selected = new ArrayList<>(outChannelList.getSelectionModel().getSelectedItems());
            if (!selected.isEmpty()) {
                controller.addUsersToChannel(selected);
                outChannelList.getItems().removeAll(selected);
                inChannelList.getItems().addAll(selected);
                outChannelList.getSelectionModel().clearSelection();
            }
        });

        removeBtn.setOnAction(e -> {
            List<User> selected = new ArrayList<>(inChannelList.getSelectionModel().getSelectedItems());
            if (!selected.isEmpty()) {
                controller.removeUsersFromChannel(selected);
                inChannelList.getItems().removeAll(selected);
                outChannelList.getItems().addAll(selected);
                inChannelList.getSelectionModel().clearSelection();
            }
        });

        VBox leftBox = new VBox(5, new Label("Non membres :"), outChannelList);
        VBox rightBox = new VBox(5, new Label("Membres actuels :"), inChannelList);
        VBox centerBox = new VBox(10, addBtn, removeBtn);
        centerBox.setAlignment(Pos.CENTER);

        HBox content = new HBox(15, leftBox, centerBox, rightBox);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}