package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.observer.IEasterEggObserver;
import com.ubo.tp.message.ihm.MainPanelViewFx;
import com.ubo.tp.message.ihm.channel.fx.ChannelListViewFx;
import com.ubo.tp.message.ihm.message.fx.ChatViewFx;
import com.ubo.tp.message.ihm.message.fx.EasterEggAnimationFx;
import com.ubo.tp.message.ihm.message.fx.MessageListViewFx;
import com.ubo.tp.message.ihm.user.fx.UserListViewFx;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.List;

/**
 * Superviseur centralisé des animations et Easter Eggs.
 */
public class EasterEggManagerFx implements IEasterEggObserver {

    private final MainPanelViewFx mainView;
    private final ChatViewFx chatView;
    private final ChannelListViewFx channelListView;
    private final UserListViewFx userListView;
    private final MessageListViewFx messageListView;

    public EasterEggManagerFx(MainPanelViewFx mainView, ChatViewFx chatView,
                              ChannelListViewFx channelListView, UserListViewFx userListView) {
        this.mainView = mainView;
        this.chatView = chatView;
        this.channelListView = channelListView;
        this.userListView = userListView;
        this.messageListView = chatView.getMessageListView();
    }

    @Override
    public void onEasterEggTriggered(String command) {
        Platform.runLater(() -> {
            switch (command) {
                case "/flash" -> EasterEggAnimationFx.playFlash(mainView);
                case "/earthquake" -> EasterEggAnimationFx.playEarthquake(chatView);
                case "/flip" -> EasterEggAnimationFx.playFlip(chatView);
                case "/party" -> playPartyOnAllLists();
                case "/detach" -> playDetachOnAllLists();
                case "/dvd" -> EasterEggAnimationFx.playDvd(chatView);
                case "/zerog" -> playZeroGOnAllLists();
                case "/matrix" -> EasterEggAnimationFx.playMatrix(mainView);
                case "/all" -> {
                    EasterEggAnimationFx.playFlash(mainView);
                    EasterEggAnimationFx.playEarthquake(chatView);
                    EasterEggAnimationFx.playFlip(chatView);
                    EasterEggAnimationFx.playDvd(chatView);
                    playPartyOnAllLists();
                    playDetachOnAllLists();
                    playZeroGOnAllLists();
                }
            }
        });
    }

    /**
     * Méthode utilitaire pour appliquer le Party aux 3 listes
     */
    private void playPartyOnAllLists() {
        EasterEggAnimationFx.playParty(channelListView);
        EasterEggAnimationFx.playParty(userListView);
        EasterEggAnimationFx.playParty(messageListView);
    }

    /**
     * Méthode utilitaire pour appliquer le Detach aux 3 listes avec le délai
     */
    private void playDetachOnAllLists() {
        PauseTransition delay = new PauseTransition(javafx.util.Duration.millis(100));
        delay.setOnFinished(e -> {
            applyDetachToCells(channelListView.getChannelCell());
            applyDetachToCells(userListView.getActiveCells());
            applyDetachToCells(messageListView.getActiveCells());
        });
        delay.play();
    }

    private void playZeroGOnAllLists() {
        PauseTransition delay = new PauseTransition(javafx.util.Duration.millis(100));
        delay.setOnFinished(e -> {
            applyZeroGToCells(channelListView.getChannelCell());
            applyZeroGToCells(userListView.getActiveCells());
            applyZeroGToCells(messageListView.getActiveCells());
        });
        delay.play();
    }

    /**
     * Applique l'effet de détachement aux N dernières cellules d'une liste pour des raisons de performance.
     */
    private void applyDetachToCells(List<? extends Node> cells) {
        for (Node cell : cells) {
            EasterEggAnimationFx.playDetach(cell);
        }
    }

    private void applyZeroGToCells(List<? extends Node> cells) {
        for (Node cell : cells) {
            EasterEggAnimationFx.playZeroG(cell);
        }
    }
}