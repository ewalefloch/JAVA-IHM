package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.observer.IChannelSelectionObserver;
import com.ubo.tp.message.controller.observer.IRemoveUserChannelObserver;
import com.ubo.tp.message.controller.observer.IUserListObserver;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                .toList();
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

    public boolean canManageCurrentChannel() {
        if (currentChannel == null || !currentChannel.ismPrivate()) {
            return false;
        }
        return currentChannel.getCreator().getUuid().equals(getConnectedUser().getUuid());
    }

    public List<User> getUsersNotInCurrentChannel() {
        if (currentChannel == null) return new ArrayList<>();
        List<UUID> memberIds = currentChannel.getUsers().stream().map(User::getUuid).toList();
        return dataManager.getUsers().stream()
                .filter(u -> !memberIds.contains(u.getUuid()) && !u.getUuid().equals(currentChannel.getCreator().getUuid()))
                .toList();
    }

    public List<User> getUsersInCurrentChannel() {
        if (currentChannel == null) return new ArrayList<>();
        return currentChannel.getUsers().stream()
                .filter(u -> !u.getUuid().equals(currentChannel.getCreator().getUuid()))
                .toList();
    }

    public void addUsersToChannel(List<User> users) {
        if (canManageCurrentChannel()) {
            for (User u : users) {
                currentChannel.addUser(u);
            }
            dataManager.sendChannel(currentChannel);
            notifyObservers();
        }
    }

    public void removeUsersFromChannel(List<User> users) {
        if (canManageCurrentChannel()) {
            for (User u : users) {
                currentChannel.removeUser(u);
            }
            dataManager.sendChannel(currentChannel);
            notifyObservers();
            notifyRemoveUser();
        }
    }

    public boolean canLeaveCurrentChannel() {
        if (currentChannel == null || !currentChannel.ismPrivate()) {
            return false;
        }
        return !currentChannel.getCreator().getUuid().equals(getConnectedUser().getUuid());
    }

    public void leaveCurrentChannel() {
        if (canLeaveCurrentChannel()) {
            currentChannel.removeUser(getConnectedUser());

            dataManager.sendChannel(currentChannel);

            currentChannel = null;

            notifyObservers();
            notifyRemoveUser();
        }
    }


    @Override
    public void onChannelSelected(Channel channel) {
        this.currentChannel = channel;
        notifyObservers();
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        //Ignore
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        //Ignore
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        //Ignore
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
        //Ignore
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        //Ignore
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        //Ignore
    }
}