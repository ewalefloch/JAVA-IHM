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

public class ChannelListController implements IDatabaseObserver, IChannelActionObserver {

    private final DataManager dataManager;
    private final ISession session;

    private final List<IChannelListObserver> observers = new ArrayList<>();
    private final List<IChannelSelectionObserver> selectionObservers = new ArrayList<>();

    private Channel currentSelectedChannel;
    private final Set<java.util.UUID> unreadChannels = new java.util.HashSet<>();

    //SRS-MAP-USR-001
    public ChannelListController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;
        dataManager.addObserver(this);
    }

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

    public String createChannel(String name, boolean isPrivate, List<User> selectedUsers) {
        if (name == null || name.trim().isEmpty()) {
            return "Le nom du canal est obligatoire.";
        }

        if (isPrivate && (selectedUsers == null || selectedUsers.isEmpty())) {
            return "Veuillez sélectionner au moins un invité pour un canal privé.";
        }

        Channel newChannel;
        User creator = session.getConnectedUser();

        if (isPrivate) {
            newChannel = new Channel(creator, name.trim(), selectedUsers);
        } else {
            newChannel = new Channel(creator, name.trim());
        }

        dataManager.sendChannel(newChannel);

        return null;
    }

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

    public void selectChannel(Channel channel) {
        this.currentSelectedChannel = channel;

        if (unreadChannels.contains(channel.getUuid())) {
            unreadChannels.remove(channel.getUuid());
            notifyObservers();
        }

        for (IChannelSelectionObserver observer : selectionObservers) {
            observer.onChannelSelected(channel);
        }
    }

    public boolean hasUnreadMessages(Channel channel) {
        return unreadChannels.contains(channel.getUuid());
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        UUID recipientUuid = addedMessage.getRecipient();

        if (currentSelectedChannel == null || !currentSelectedChannel.getUuid().equals(recipientUuid)) {
            unreadChannels.add(recipientUuid);
            notifyObservers();
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        //IGNORE
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        //IGNORE
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        //IGNORE
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        //IGNORE
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        //IGNORE
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        notifyObservers();
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        notifyObservers();
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        notifyObservers();
    }

    public Set<User> getAllUsers() {
        return dataManager.getUsers();
    }

    @Override
    public void onDeleteRequested(Channel channel) {
        dataManager.deleteChannel(channel);
    }

    public boolean isMyChannel(Channel channel) {
        return channel.getCreator().getUuid().equals(session.getConnectedUser().getUuid());
    }
}
