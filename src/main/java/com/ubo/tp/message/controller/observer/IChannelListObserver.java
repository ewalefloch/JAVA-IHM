package com.ubo.tp.message.controller.observer;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;

public interface IChannelListObserver {
    void onChannelListChanged();

    default void onNotificationTriggered(Message message, Channel targetChannel, boolean isMention) {}
}
