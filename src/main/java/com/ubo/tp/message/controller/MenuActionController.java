package main.java.com.ubo.tp.message.controller;

import javafx.application.Platform;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.Set;

public class MenuActionController {
    private final DataManager mDataManager;
    private final ISession session;

    public MenuActionController(DataManager dataManager, ISession session) {
        this.mDataManager = dataManager;
        this.session = session;
    }

    public void logout() {
        User currentUser = session.getConnectedUser();

        if (currentUser != null) {
            currentUser.setOnline(false);
            session.disconnect();
            mDataManager.sendUser(currentUser);
        }
    }

    public void exit(){
        logout();
        Platform.exit(); // Fermer proprement le thread JavaFX
        System.exit(0);
    }

    public boolean deleteAccount() {
        User currentUser = session.getConnectedUser();
        if (currentUser == null) return false;

        User unknownUser = mDataManager.getUsers().stream().filter(u -> u.getUserTag().equals("<Inconnu>")).findFirst().get();
        Set<Message> userMessages = mDataManager.getMessagesFrom(currentUser);

        for (Message oldMessage : userMessages) {
            Message anonymizedMessage = new Message(
                    oldMessage.getUuid(),
                    unknownUser,
                    oldMessage.getRecipient(),
                    oldMessage.getEmissionDate(),
                    oldMessage.getText()
            );
            mDataManager.sendMessage(anonymizedMessage);
        }

        mDataManager.deleteUser(currentUser);
        this.logout();
        return true;
    }

    public boolean modifyUserTag(String name) {
        User currentUser = session.getConnectedUser();
        if (currentUser == null) return false;

        currentUser.setName(name);
        mDataManager.sendUser(currentUser);

        return true;
    }
}
