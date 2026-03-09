package com.ubo.tp.message.controller;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.channel.ChannelListView;
import com.ubo.tp.message.ihm.message.ChatView;
import com.ubo.tp.message.ihm.user.UserListView;

import java.util.Set;

public class MainPanelController {
    private final DataManager dataManager;
    private final ISession session;
    private final ChannelListController channelListController;
    private final UserListController userListController;
    private final MessageListController messageListController;

    private final ChannelListView channelListView;
    private final UserListView userListView;
    private final ChatView chatView;

    public MainPanelController(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;

        this.messageListController = new MessageListController(dataManager, session);
        this.channelListController = new ChannelListController(dataManager, session);
        this.userListController = new UserListController(dataManager,session);
        initDefaultChannel();
        this.chatView = new ChatView(messageListController);
        this.channelListView = new ChannelListView(channelListController);
        this.userListView = new UserListView(userListController);

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

        } else {
            this.messageListController.setCurrentChannel(channels.iterator().next());
        }
    }


    public ChannelListView getChannelListView() {
        return channelListView;
    }

    public UserListView getUserListView() {
        return userListView;
    }

    public ChatView getChatView() {
        return chatView;
    }
}