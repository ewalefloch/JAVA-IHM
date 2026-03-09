package com.ubo.tp.message.ihm.message.fx;

import com.ubo.tp.message.controller.observer.IMessageActionObserver;
import javafx.scene.layout.BorderPane;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.common.fx.AbstractListViewFx;

import java.util.ArrayList;
import java.util.List;

public class MessageListViewFx extends AbstractListViewFx<Message> {

    private final List<IMessageActionObserver> observers = new ArrayList<>();

    public MessageListViewFx() {
        super("Discussion");
    }

    public void addObserver(IMessageActionObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    protected BorderPane createCell(Message item) {
        boolean isMe = observers.stream().anyMatch(obs -> obs.isMyMessage(item));

        return new MessageCellViewFx(item, isMe, () -> {
            for (IMessageActionObserver obs : observers) {
                obs.onDeleteRequested(item);
            }
        });
    }

    @Override
    protected String getEmptyMessage() {
        return "Aucun message dans ce canal.";
    }

    @Override
    protected int getCellHeight() {
        return 80;
    }

    public void refresh(List<Message> messages) {
        updateList(messages);
    }

    @Override
    protected boolean matchSearch(Message item, String query) {
        return item.getText() != null && item.getText().toLowerCase().contains(query);
    }
}