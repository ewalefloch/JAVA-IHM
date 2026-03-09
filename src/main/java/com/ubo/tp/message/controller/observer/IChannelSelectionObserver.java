package com.ubo.tp.message.controller.observer;

import com.ubo.tp.message.datamodel.Channel;

public interface IChannelSelectionObserver {
    void onChannelSelected(Channel channel);
}