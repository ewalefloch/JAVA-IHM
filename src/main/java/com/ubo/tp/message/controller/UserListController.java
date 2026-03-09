package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.controller.observer.IChannelSelectionObserver;
import main.java.com.ubo.tp.message.controller.observer.IRemoveUserChannelObserver;
import main.java.com.ubo.tp.message.controller.observer.IUserListObserver;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion de la liste des utilisateurs.
 * Implémente IDatabaseObserver pour être notifié des changements.
 */
public class UserListController implements IDatabaseObserver, IChannelSelectionObserver {

    private final DataManager dataManager;
    private final ISession session;
    private Channel currentChannel;

    private final List<IUserListObserver> observers = new ArrayList<>();
    private final List<IRemoveUserChannelObserver> observersRemoveUser = new ArrayList<>();

    //SRS-MAP-USR-007
    public UserListController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;

        // S'enregistrer comme observateur de la base de données
        dataManager.addObserver(this);
    }

    /**
     * Retourne la liste de tous les utilisateurs.
     */
    public List<User> getUsers() {
        return dataManager.getUsers().stream()
                .filter(u -> {
                    if (currentChannel == null) return true;

                    if (!currentChannel.ismPrivate()) return true;

                    boolean isCreator = currentChannel.getCreator().getUuid().equals(u.getUuid());
                    boolean isInvited = currentChannel.getUsers().stream()
                            .anyMatch(member -> member.getUuid().equals(u.getUuid()));

                    return isCreator || isInvited;
                })
                .sorted((u1, u2) -> {
                    if (u1.isOnline() == u2.isOnline()) {
                        return u1.getName().compareToIgnoreCase(u2.getName());
                    }
                    return u1.isOnline() ? -1 : 1;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retourne l'utilisateur connecté.
     */
    public User getConnectedUser() {
        return session.getConnectedUser();
    }

    /**
     * Ajoute un observateur.
     */
    public void addObserver(IUserListObserver observer) {
        this.observers.add(observer);
        observer.onUserListChanged();
    }

    public void addObserverRemoveUser(IRemoveUserChannelObserver observer) {
        this.observersRemoveUser.add(observer);
    }

    public void notifyRemoveUser(){
        for (IRemoveUserChannelObserver observer : observersRemoveUser){
            observer.onemoveUserChannel();
        }
    }

    /**
     * Supprime un observateur.
     */
    public void removeObserver(IUserListObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Notifie tous les observateurs des changements.
     */
    protected void notifyObservers() {
        for (IUserListObserver observer : observers) {
            observer.onUserListChanged();
        }
    }

    @Override
    public void onChannelSelected(Channel channel) {
        this.currentChannel = channel;
        notifyObservers();
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        notifyObservers();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        notifyObservers();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        notifyObservers();
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
    }
}