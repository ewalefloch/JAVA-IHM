package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.observer.IChannelSelectionObserver;
import com.ubo.tp.message.controller.observer.IMessageActionObserver;
import com.ubo.tp.message.controller.observer.IMessageListObserver;
import com.ubo.tp.message.controller.observer.IUserSelectionObserver;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageListController implements IDatabaseObserver, IChannelSelectionObserver, IMessageActionObserver {

    private final DataManager dataManager;
    private final ISession session;
    private final List<IMessageListObserver> observers = new ArrayList<>();
    private Channel currentChannel;

    public MessageListController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;
        // S'enregistrer comme observateur de la base de données
        this.dataManager.addObserver(this);
    }

    /**
     * Retourne la liste message d'un channel
     */
    //SRS-MAP-MSG-001
    public List<Message> getMessages() {
        return dataManager.getMessagesByChannel(currentChannel).stream()
                .sorted(Comparator.comparingLong(Message::getEmissionDate))
                .collect(Collectors.toList());
    }

    /**
     * Envoie un nouveau message.
     */
    //SRS-MAP-MSG-002
    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        //SRS-MAP-MSG-008
        if (text.length() > 200){
            return;
        }

        User sender = session.getConnectedUser();
        Message newMessage = new Message(sender, currentChannel.getUuid(), text);

        dataManager.sendMessage(newMessage);
    }

    public void addObserver(IMessageListObserver observer) {
        this.observers.add(observer);
        observer.onMessageListChanged();
    }

    public void removeObserver(IMessageListObserver observer) {
        this.observers.remove(observer);
    }

    protected void notifyObservers() {
        for (IMessageListObserver observer : observers) {
            observer.onMessageListChanged();
        }
    }

    public void setCurrentChannel(Channel currentChannel) {
        this.currentChannel = currentChannel;
    }

    @Override
    public void onChannelSelected(Channel channel) {
        this.setCurrentChannel(channel);
        this.notifyObservers();
    }

    // --- Implémentation de IDatabaseObserver ---

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        notifyObservers();
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        notifyObservers();
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        notifyObservers();
    }

    // ignore
    @Override
    public void notifyUserAdded(User addedUser) {/* IGNORE */}
    @Override
    public void notifyUserDeleted(User deletedUser) {/* IGNORE */}
    @Override
    public void notifyUserModified(User modifiedUser) {/* IGNORE */}
    @Override
    public void notifyChannelAdded(Channel addedChannel) {/* IGNORE */}
    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {/* IGNORE */}
    @Override
    public void notifyChannelModified(Channel modifiedChannel) { /* IGNORE */ }

    @Override
    public void onDeleteRequested(Message message) {
        dataManager.deleteMessage(message);
    }

    @Override
    public boolean isMyMessage(Message message) {
        return session.getConnectedUser().getUuid().equals(message.getSender().getUuid());
    }
}