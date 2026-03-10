package com.ubo.tp.message.controller;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.channel.fx.ChannelListViewFx;
import com.ubo.tp.message.ihm.message.fx.ChatViewFx;
import com.ubo.tp.message.ihm.message.fx.MessageListViewFx;
import com.ubo.tp.message.ihm.message.fx.NotificationPopupFx;
import com.ubo.tp.message.ihm.user.fx.UserListViewFx;

import java.util.Set;

/**
 * Contrôleur principal de l'application gérant l'orchestration des sous-contrôleurs et des vues.
 */
public class MainPanelControllerFx {
    private final DataManager dataManager;
    private final ISession session;
    private final MessageListController messageListController;

    private final ChannelListViewFx channelListView;
    private final UserListViewFx userListView;
    private final ChatViewFx chatView;
    private static final String ADMIN = "admin";

    public MainPanelControllerFx(DataManager dataManager, ISession session) {
        this.dataManager = dataManager;
        this.session = session;

        // Initialisation des contrôleurs
        this.messageListController = new MessageListController(dataManager, session);
        ChannelListController channelListController = new ChannelListController(dataManager, session);
        UserListController userListController = new UserListController(dataManager, session);

        // Configuration de l'état initial
        handleDefaultChannelInitialization();

        // Orchestration des observateurs
        setupControllerInteractions(channelListController, userListController);

        // Initialisation des composants de vue
        MessageListViewFx messageListView = new MessageListViewFx();
        this.chatView = new ChatViewFx(messageListController, messageListView);

        this.channelListView = new ChannelListViewFx(channelListController);
        this.userListView = new UserListViewFx(userListController);

        // Configuration des liens vue-contrôleur restants
        setupViewInteractions(messageListView, channelListController);
    }

    // --- LOGIQUE DE TRAITEMENT (Handlers / Init) ---

    /**
     * Gère l'initialisation du canal par défaut si aucun canal n'existe.
     */
    private void handleDefaultChannelInitialization() {
        Set<Channel> channels = dataManager.getChannels(session.getConnectedUser());

        if (channels == null || channels.isEmpty()) {
            User admin = findOrCreateAdminUser();
            Channel generalChannel = new Channel(admin, "Général");
            dataManager.sendChannel(generalChannel);
            this.messageListController.setCurrentChannel(generalChannel);
        }
    }

    /**
     * Configure les interactions et abonnements entre les différents contrôleurs.
     */
    private void setupControllerInteractions(ChannelListController channelCtrl, UserListController userCtrl) {
        // Abonnements aux sélections de canaux
        channelCtrl.addSelectionObserver(this.messageListController);
        channelCtrl.addSelectionObserver(userCtrl);

        // Abonnements aux sélections d'utilisateurs (Messages Privés)
        userCtrl.addUserSelectionObserver(channelCtrl);
    }

    /**
     * Configure les interactions entre les vues et les contrôleurs.
     */
    private void setupViewInteractions(MessageListViewFx msgListView, ChannelListController channelCtrl) {
        msgListView.addObserver(messageListController);
        channelCtrl.addObserver(new NotificationPopupFx(channelCtrl));
    }

    /**
     * Recherche un utilisateur existant ou crée un compte administrateur par défaut.
     */
    private User findOrCreateAdminUser() {
        Set<User> users = dataManager.getUsers();
        if (users == null || users.isEmpty()) {
            User admin = new User(ADMIN, ADMIN, ADMIN);
            dataManager.sendUser(admin);
            return admin;
        }
        return users.iterator().next();
    }

    // --- GETTERS ---

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