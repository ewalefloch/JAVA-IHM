package com.ubo.tp.message.ihm;

import com.ubo.tp.message.controller.MainPanelController;
import com.ubo.tp.message.ihm.channel.ChannelListView;
import com.ubo.tp.message.ihm.message.ChatView;
import com.ubo.tp.message.ihm.user.UserListView;

import javax.swing.*;
import java.awt.*;

public class MainPanelView extends JPanel {

    private final MainPanelController controller;

    // Composants graphiques
    private ChannelListView channelListView;
    private UserListView userListPanel;
    private ChatView chatView;
    private JPanel chatPanel;


    public MainPanelView(MainPanelController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        //COLONNE GAUCHE : Liste des Canaux
        channelListView = controller.getChannelListView();
        channelListView.setPreferredSize(new Dimension(250, 0));
        channelListView.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        add(channelListView, BorderLayout.WEST);

        //COLONNE DROITE : Liste des Utilisateurs
        userListPanel = controller.getUserListView();
        userListPanel.setPreferredSize(new Dimension(250, 0));
        userListPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        add(userListPanel, BorderLayout.EAST);

        //CENTRE : Zone de Chat
        chatView = controller.getChatView();

        add(chatView, BorderLayout.CENTER);
    }
}