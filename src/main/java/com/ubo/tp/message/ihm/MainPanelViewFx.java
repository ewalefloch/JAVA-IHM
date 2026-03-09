package com.ubo.tp.message.ihm;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import com.ubo.tp.message.controller.MainPanelControllerFx;
import com.ubo.tp.message.ihm.channel.fx.ChannelListViewFx;
import com.ubo.tp.message.ihm.message.fx.ChatViewFx;
import com.ubo.tp.message.ihm.user.fx.UserListViewFx;

public class MainPanelViewFx extends BorderPane {

    private final ChannelListViewFx channelListView;
    private final UserListViewFx userListView;
    private final ChatViewFx chatView;

    public MainPanelViewFx(MainPanelControllerFx mainPanelControllerFx) {
        channelListView = mainPanelControllerFx.getChannelListView();
        userListView = mainPanelControllerFx.getUserListView();
        chatView = mainPanelControllerFx.getChatView();
        // COLONNE GAUCHE
        channelListView.setPrefWidth(250);
        channelListView.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 1 0 0;");
        this.setLeft(channelListView);

        // COLONNE DROITE
        userListView.setPrefWidth(250);
        userListView.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 0 1;");
        this.setRight(userListView);

        // CENTRE
        this.setCenter(chatView);
    }

}