package com.ubo.tp.message.controller.observer;

import com.ubo.tp.message.datamodel.Channel;

public interface IUserActionObserver {
    void onDeleteRequested(Channel channel);
    boolean isMyChannel(Channel channel);
}
