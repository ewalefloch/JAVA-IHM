package com.ubo.tp.message.controller.observer;

import com.ubo.tp.message.datamodel.Message;

public interface IMessageActionObserver {
    void onDeleteRequested(Message message);
    boolean isMyMessage(Message message);
}
