package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.observer.IChannelSelectionObserver;
import com.ubo.tp.message.controller.observer.IRemoveUserChannelObserver;
import com.ubo.tp.message.controller.observer.IUserListObserver;
import com.ubo.tp.message.controller.observer.IUserSelectionObserver;
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
    private final List<IUserSelectionObserver> userSelectionObservers = new ArrayList<>();

    public UserListController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;
        dataManager.addObserver(this);
    }

    // --- POINTS D'ENTRÉE ---

    @Override
    public void onChannelSelected(Channel channel) {
        handleChannelSelection(channel);
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        handleUserListUpdate();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        handleUserListUpdate();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        handleUserListUpdate();
    }

    // --- LOGIQUE DE TRAITEMENT ---

    private void handleChannelSelection(Channel channel) {
        this.currentChannel = channel;
        this.notifyObservers();
    }

    private void handleUserListUpdate() {
        this.notifyObservers();
    }

    private void handleUserSelection(User user) {
        for (IUserSelectionObserver observer : userSelectionObservers) {
            observer.onUserSelected(user);
        }
    }

    private void handleRemoveUserChannelNotification() {
        for (IRemoveUserChannelObserver observer : observersRemoveUser) {
            observer.onemoveUserChannel();
        }
    }

    // --- LOGIQUE MÉTIER ---

    public List<User> getUsers() {
        return dataManager.getUsers().stream()
                .filter(this::isUserVisibleInCurrentContext)
                .sorted(this::compareUsersByStatusAndName)
                .toList();
    }

    private boolean isUserVisibleInCurrentContext(User u) {
        if (currentChannel == null || !currentChannel.ismPrivate()) {
            return true;
        }
        boolean isCreator = currentChannel.getCreator().getUuid().equals(u.getUuid());
        boolean isInvited = currentChannel.getUsers().stream()
                .anyMatch(member -> member.getUuid().equals(u.getUuid()));
        return isCreator || isInvited;
    }

    private int compareUsersByStatusAndName(User u1, User u2) {
        if (u1.isOnline() == u2.isOnline()) {
            return u1.getName().compareToIgnoreCase(u2.getName());
        }
        return u1.isOnline() ? -1 : 1;
    }

    public void selectUserForMessaging(User user) {
        handleUserSelection(user);
    }

    public void addUsersToChannel(List<User> users) {
        if (canManageCurrentChannel()) {
            users.forEach(currentChannel::addUser);
            dataManager.sendChannel(currentChannel);
            handleUserListUpdate();
        }
    }

    public void removeUsersFromChannel(List<User> users) {
        if (canManageCurrentChannel()) {
            users.forEach(currentChannel::removeUser);
            dataManager.sendChannel(currentChannel);
            handleUserListUpdate();
            handleRemoveUserChannelNotification();
        }
    }

    public void leaveCurrentChannel() {
        if (canLeaveCurrentChannel()) {
            currentChannel.removeUser(getConnectedUser());
            dataManager.sendChannel(currentChannel);
            this.currentChannel = null;
            handleUserListUpdate();
            handleRemoveUserChannelNotification();
        }
    }

    // --- UTILITAIRES ET GESTION OBSERVATEURS ---

    public User getConnectedUser() { return session.getConnectedUser(); }

    public void addObserver(IUserListObserver observer) {
        this.observers.add(observer);
        observer.onUserListChanged();
    }

    public void removeObserver(IUserListObserver observer) { this.observers.remove(observer); }

    protected void notifyObservers() {
        for (IUserListObserver observer : observers) {
            observer.onUserListChanged();
        }
    }

    public void addObserverRemoveUser(IRemoveUserChannelObserver obs) { this.observersRemoveUser.add(obs); }
    public void addUserSelectionObserver(IUserSelectionObserver obs) { this.userSelectionObservers.add(obs); }

    public boolean canManageCurrentChannel() {
        return currentChannel != null && currentChannel.ismPrivate() &&
                currentChannel.getCreator().getUuid().equals(getConnectedUser().getUuid());
    }

    public boolean canLeaveCurrentChannel() {
        return currentChannel != null && currentChannel.ismPrivate() &&
                !currentChannel.getCreator().getUuid().equals(getConnectedUser().getUuid());
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

    @Override public void notifyMessageAdded(Message m) {
        //ignore
    }
    @Override public void notifyMessageDeleted(Message m) {
        //ignore
    }
    @Override public void notifyMessageModified(Message m) {
        //ignore
    }
    @Override public void notifyChannelAdded(Channel c) {
        //ignore
    }
    @Override public void notifyChannelDeleted(Channel c) {
        //ignore
    }
    @Override public void notifyChannelModified(Channel c) {
        //ignore
    }
}