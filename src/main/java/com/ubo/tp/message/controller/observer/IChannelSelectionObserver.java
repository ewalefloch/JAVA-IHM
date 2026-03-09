package main.java.com.ubo.tp.message.controller.observer;

import main.java.com.ubo.tp.message.datamodel.Channel;

public interface IChannelSelectionObserver {
    void onChannelSelected(Channel channel);
}