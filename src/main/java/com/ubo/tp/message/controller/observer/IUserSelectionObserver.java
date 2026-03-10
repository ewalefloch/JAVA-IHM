package com.ubo.tp.message.controller.observer;

import com.ubo.tp.message.datamodel.User;

public interface IUserSelectionObserver {
    void onUserSelected(User user);
}