package main.java.com.ubo.tp.message.ihm.user;

import main.java.com.ubo.tp.message.controller.UserListController;
import main.java.com.ubo.tp.message.controller.observer.IUserListObserver;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.AbstractListView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class UserListView extends AbstractListView<User> implements IUserListObserver {

    private final UserListController controller;
    private UserCellView selectedUserCell;

    public UserListView(UserListController controller) {
        super("Utilisateurs");
        this.controller = controller;
        this.controller.addObserver(this);
    }

    @Override
    protected JPanel createCell(User item) {
        UserCellView cell = new UserCellView(item);

        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectUser(cell);
            }
        });

        return cell;
    }

    private void selectUser(UserCellView userCell) {
        if (selectedUserCell != null) {
            selectedUserCell.setSelected(false);
        }
        selectedUserCell = userCell;
        userCell.setSelected(true);
        // TODO: Notifier le contrôleur de la sélection si nécessaire
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
        updateList(users);
    }

    @Override
    protected boolean matchSearch(User item, String query) {
        boolean matchName = item.getName() != null && item.getName().toLowerCase().contains(query);
        boolean matchTag = item.getUserTag() != null && item.getUserTag().toLowerCase().contains(query);
        return matchName || matchTag;
    }
}