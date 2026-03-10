package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.observer.*;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChannelListController implements IDatabaseObserver, IChannelActionObserver, IUserSelectionObserver {

    private final DataManager dataManager;
    private final ISession session;

    private final List<IChannelListObserver> observers = new ArrayList<>();
    private final List<IChannelSelectionObserver> selectionObservers = new ArrayList<>();

    private Channel currentSelectedChannel;
    private final Set<java.util.UUID> unreadChannels = new java.util.HashSet<>();

    public ChannelListController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;
        dataManager.addObserver(this);
    }

    // --- Méthodes Publiques ---

    public List<Channel> getChannels() {
        User currentUser = session.getConnectedUser();
        if (currentUser == null) return new ArrayList<>();
        return new ArrayList<>(dataManager.getChannels(currentUser));
    }

    public void deleteChannel(Channel channel) {
        dataManager.deleteChannel(channel);
    }

    public boolean isChannelPrivate(Channel channel) {
        return !channel.getUsers().isEmpty();
    }

    public boolean isMyChannel(Channel channel) {
        User me = session.getConnectedUser();
        return me != null && channel.getCreator().getUuid().equals(me.getUuid());
    }

    public boolean hasUnreadMessages(Channel channel) {
        return unreadChannels.contains(channel.getUuid());
    }

    public Set<User> getAllUsers() {
        return dataManager.getUsers();
    }

    public String createChannel(String name, boolean isPrivate, List<User> selectedUsers) {
        if (name == null || name.trim().isEmpty()) {
            return "Le nom du canal est obligatoire.";
        }

        if (isPrivate && (selectedUsers == null || selectedUsers.isEmpty())) {
            return "Veuillez sélectionner au moins un invité pour un canal privé.";
        }

        User creator = session.getConnectedUser();
        Channel newChannel = isPrivate ? new Channel(creator, name.trim(), selectedUsers)
                : new Channel(creator, name.trim());

        dataManager.sendChannel(newChannel);
        return null;
    }

    public void selectChannel(Channel channel) {
        this.currentSelectedChannel = channel;
        handleChannelSelection(channel);
    }

    // --- Implémentation IDatabaseObserver ---

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        handleIncomingMessage(addedMessage);
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        handleChannelListChanged();
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        handleChannelListChanged();
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        handleChannelListChanged();
    }

    // --- Implémentation IUserSelectionObserver ---

    @Override
    public void onUserSelected(User selectedUser) {
        handlePrivateConversationRequest(selectedUser);
    }

    // --- Implémentation IChannelActionObserver ---

    @Override
    public void onDeleteRequested(Channel channel) {
        deleteChannel(channel);
    }

    // --- Logique Métier ---

    private void handleIncomingMessage(Message message) {
        UUID recipientUuid = message.getRecipient();
        User me = session.getConnectedUser();

        // 1. Mise à jour de l'état de lecture
        if (currentSelectedChannel == null || !currentSelectedChannel.getUuid().equals(recipientUuid)) {
            unreadChannels.add(recipientUuid);
            notifyObservers();
        }

        // 2. Traitement des notifications (Mentions @)
        if (me != null && !message.getSender().getUuid().equals(me.getUuid())) {
            processPotentialNotification(message, recipientUuid);
        }
    }

    private void processPotentialNotification(Message message, UUID recipientUuid) {
        Channel targetChannel = getChannels().stream()
                .filter(c -> c.getUuid().equals(recipientUuid))
                .findFirst()
                .orElse(null);

        if (targetChannel != null && message.getText().contains("@")) {
            for (IChannelListObserver obs : observers) {
                obs.onNotificationTriggered(message, targetChannel, true);
            }
        }
    }

    private void handleChannelSelection(Channel channel) {
        if (unreadChannels.contains(channel.getUuid())) {
            unreadChannels.remove(channel.getUuid());
            notifyObservers();
        }

        for (IChannelSelectionObserver observer : selectionObservers) {
            observer.onChannelSelected(channel);
        }
    }

    private void handleChannelListChanged() {
        notifyObservers();
    }

    private void handlePrivateConversationRequest(User selectedUser) {
        User me = session.getConnectedUser();
        if (me == null) return;

        // On cherche un canal privé existant avec seulement cet utilisateur
        Channel privateChannel = dataManager.getChannels().stream()
                .filter(c -> c.ismPrivate() && c.getUsers().size() == 1)
                .filter(c -> {
                    User guest = c.getUsers().get(0);
                    boolean amICreator = c.getCreator().getUuid().equals(me.getUuid()) && guest.getUuid().equals(selectedUser.getUuid());
                    boolean isHeCreator = c.getCreator().getUuid().equals(selectedUser.getUuid()) && guest.getUuid().equals(me.getUuid());
                    return amICreator || isHeCreator;
                })
                .findFirst()
                .orElse(null);

        if (privateChannel == null) {
            List<User> guests = new ArrayList<>();
            guests.add(selectedUser);
            privateChannel = new Channel(me, "MP - " + selectedUser.getName(), guests);
            dataManager.sendChannel(privateChannel);
        }

        selectChannel(privateChannel);
    }

    // --- Gestion des Observateurs ---

    public void addObserver(IChannelListObserver observer) {
        this.observers.add(observer);
        observer.onChannelListChanged();
    }

    public void removeObserver(IChannelListObserver observer) {
        this.observers.remove(observer);
    }

    protected void notifyObservers() {
        for (IChannelListObserver observer : observers) {
            observer.onChannelListChanged();
        }
    }

    public void addSelectionObserver(IChannelSelectionObserver observer) {
        this.selectionObservers.add(observer);
    }

    // --- Méthodes Ignorées ---
    @Override public void notifyMessageDeleted(Message deletedMessage) {
        //ignore
    }
    @Override public void notifyMessageModified(Message modifiedMessage) {
        //ignore
    }
    @Override public void notifyUserAdded(User addedUser) {
        //ignore
    }
    @Override public void notifyUserDeleted(User deletedUser) {
        //ignore
    }
    @Override public void notifyUserModified(User modifiedUser) {
        //ignore
    }
}