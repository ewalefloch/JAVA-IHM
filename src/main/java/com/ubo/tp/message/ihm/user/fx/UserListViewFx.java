package com.ubo.tp.message.ihm.user.fx;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import com.ubo.tp.message.controller.UserListController;
import com.ubo.tp.message.controller.observer.IUserListObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.common.fx.AbstractListViewFx;

import java.util.List;

public class UserListViewFx extends AbstractListViewFx<User> implements IUserListObserver {

    private final UserListController controller;
    private UserCellViewFx selectedUserCell;

    public UserListViewFx(UserListController controller) {
        super("Utilisateurs");
        this.controller = controller;
        this.controller.addObserver(this);
    }

    @Override
    protected BorderPane createCell(User item) {
        UserCellViewFx cell = new UserCellViewFx(item);

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
        Platform.runLater(() -> updateList(users));
    }

    @Override
    protected boolean matchSearch(User item, String query) {
        boolean matchName = item.getName() != null && item.getName().toLowerCase().contains(query);
        boolean matchTag = item.getUserTag() != null && item.getUserTag().toLowerCase().contains(query);
        return matchName || matchTag;
    }
}