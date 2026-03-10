package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.observer.IChannelSelectionObserver;
import com.ubo.tp.message.controller.observer.IEasterEggObserver;
import com.ubo.tp.message.controller.observer.IMessageActionObserver;
import com.ubo.tp.message.controller.observer.IMessageListObserver;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion de la liste des messages.
 */
public class MessageListController implements IDatabaseObserver, IChannelSelectionObserver, IMessageActionObserver {

    private final DataManager dataManager;
    private final ISession session;
    private final List<IMessageListObserver> observers = new ArrayList<>();
    private final List<IEasterEggObserver> easterEggObservers = new ArrayList<>();
    private Channel currentChannel;

    public MessageListController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;
        this.dataManager.addObserver(this);
    }

    // --- POINTS D'ENTRÉE  ---

    @Override
    public void onChannelSelected(Channel channel) {
        handleChannelSelection(channel);
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        handleIncomingMessage(addedMessage);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        handleMessageListChange();
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        handleMessageListChange();
    }

    @Override
    public void onDeleteRequested(Message message) {
        handleMessageDeletion(message);
    }

    // --- LOGIQUE DE TRAITEMENT  ---

    private void handleChannelSelection(Channel channel) {
        this.currentChannel = channel;
        this.notifyObservers();
    }

    private void handleMessageListChange() {
        this.notifyObservers();
    }

    private void handleMessageDeletion(Message message) {
        dataManager.deleteMessage(message);
    }

    private void handleIncomingMessage(Message message) {
        if (currentChannel != null && currentChannel.getUuid().equals(message.getRecipient())) {
            handleMessageListChange();

            String text = message.getText().trim().toLowerCase();
            if (text.startsWith("/")) {
                handleEasterEggCommand(text);
            }
        }
    }

    private void handleEasterEggCommand(String command) {
        for (IEasterEggObserver obs : easterEggObservers) {
            obs.onEasterEggTriggered(command);
        }
    }

    // --- LOGIQUE MÉTIER ---

    /**
     * Retourne la liste des messages du canal actuel triée par date.
     */
    public List<Message> getMessages() {
        return dataManager.getMessagesByChannel(currentChannel).stream()
                .sorted(Comparator.comparingLong(Message::getEmissionDate))
                .collect(Collectors.toList());
    }

    /**
     * Valide et envoie un nouveau message.
     */
    public void sendMessage(String text) {
        if (isInputInvalid(text)) {
            return;
        }

        User sender = session.getConnectedUser();
        Message newMessage = new Message(sender, currentChannel.getUuid(), text.trim());

        dataManager.sendMessage(newMessage);
    }

    /**
     * Retourne la liste des utilisateurs suggérés pour une mention (@).
     */
    public List<User> getCurrentChannelUsers(String query) {
        if (currentChannel == null) return new ArrayList<>();

        List<User> availableUsers = getAvailableUsersForCurrentChannel();

        return availableUsers.stream()
                .filter(u -> !u.getUuid().equals(session.getConnectedUser().getUuid()))
                .filter(u -> matchesMentionQuery(u, query))
                .toList();
    }

    public void setCurrentChannel(Channel channel) {
        this.currentChannel = channel;
        this.notifyObservers();
    }

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    private boolean isInputInvalid(String text) {
        return text == null || text.trim().isEmpty() || text.length() > 200;
    }

    private List<User> getAvailableUsersForCurrentChannel() {
        if (currentChannel.ismPrivate()) {
            List<User> users = new ArrayList<>(currentChannel.getUsers());
            users.add(currentChannel.getCreator());
            return users;
        }
        return new ArrayList<>(dataManager.getUsers());
    }

    private boolean matchesMentionQuery(User user, String query) {
        if (query == null || query.trim().isEmpty()) return true;

        String lowerQuery = query.toLowerCase();
        return user.getUserTag().toLowerCase().startsWith(lowerQuery) ||
                user.getName().toLowerCase().startsWith(lowerQuery);
    }

    public void addObserver(IMessageListObserver observer) {
        this.observers.add(observer);
        observer.onMessageListChanged();
    }

    public void removeObserver(IMessageListObserver observer) {
        this.observers.remove(observer);
    }

    public void addEasterEggObserver(IEasterEggObserver observer) {
        this.easterEggObservers.add(observer);
    }

    public void removeObserver(IEasterEggObserver observer) {
        this.easterEggObservers.remove(observer);
    }

    protected void notifyObservers() {
        for (IMessageListObserver observer : observers) {
            observer.onMessageListChanged();
        }
    }

    @Override
    public boolean isMyMessage(Message message) {
        return session.getConnectedUser().getUuid().equals(message.getSender().getUuid());
    }

    // Méthodes de l'interface IDatabaseObserver ignorées dans ce contrôleur
    @Override public void notifyUserAdded(User addedUser) {
        //ignore
    }
    @Override public void notifyUserDeleted(User deletedUser) {
        //ignore
    }
    @Override public void notifyUserModified(User modifiedUser) {
        //ignore
    }
    @Override public void notifyChannelAdded(Channel addedChannel) {
        //ignore
    }
    @Override public void notifyChannelDeleted(Channel deletedChannel) {
        //ignore
    }
    @Override public void notifyChannelModified(Channel modifiedChannel) {
        //ignore
    }
}