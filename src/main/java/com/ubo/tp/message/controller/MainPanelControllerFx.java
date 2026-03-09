package com.ubo.tp.message.controller;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.channel.ChannelListView;
import com.ubo.tp.message.ihm.channel.fx.ChannelListViewFx;
import com.ubo.tp.message.ihm.message.ChatView;
import com.ubo.tp.message.ihm.message.fx.ChatViewFx;
import com.ubo.tp.message.ihm.user.UserListView;
import com.ubo.tp.message.ihm.user.fx.UserListViewFx;

import java.util.Set;

public class MainPanelControllerFx {
    private final DataManager dataManager;
    private final ISession session;
    private final ChannelListController channelListController;
    private final UserListController userListController;
    private final MessageListController messageListController;

    private final ChannelListViewFx channelListView;
    private final UserListViewFx userListView;
    private final ChatViewFx chatView;

    public MainPanelControllerFx(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;

        this.messageListController = new MessageListController(dataManager, session);
        this.channelListController = new ChannelListController(dataManager, session);
        this.userListController = new UserListController(dataManager,session);

        initDefaultChannel();

        this.channelListController.addSelectionObserver(this.messageListController);
        this.channelListController.addSelectionObserver(this.userListController);

        this.chatView = new ChatViewFx(messageListController);
        this.channelListView = new ChannelListViewFx(channelListController);
        this.userListView = new UserListViewFx(userListController);

    }
    private void initDefaultChannel() {
        Set<Channel> channels = dataManager.getChannels(session.getConnectedUser());

        if (channels == null || channels.isEmpty() ) {
            Set<User> users  = dataManager.getUsers();
            User user = null;
            if (users == null || users.isEmpty()){
                user = new User("admin", "admin","admin");
                dataManager.sendUser(user);
            }else {
                user = users.iterator().next();
            }
            Channel generalChannel = new Channel(user,"Général");
            dataManager.sendChannel(generalChannel);
            this.messageListController.setCurrentChannel(generalChannel);

        }
    }

    public ChannelListViewFx getChannelListView() {
        return channelListView;
    }

    public UserListViewFx getUserListView() {
        return userListView;
    }

    public ChatViewFx getChatView() {
        return chatView;
    }
}