package com.ubo.tp.message.ihm.message.fx;

import com.ubo.tp.message.controller.ChannelListController;
import com.ubo.tp.message.controller.observer.IChannelListObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Composant gérant l'affichage des notifications surgissantes (popups).
 */
public class NotificationPopupFx implements IChannelListObserver {

    private final ChannelListController controller;

    public NotificationPopupFx(ChannelListController controller) {
        this.controller = controller;
    }

    // --- POINTS D'ENTRÉE (Observer) ---

    @Override
    public void onChannelListChanged() {
        // Ignoré
    }

    @Override
    public void onNotificationTriggered(Message message, Channel targetChannel, boolean isMention) {
        Platform.runLater(() -> handleNotificationDisplay(message, targetChannel));
    }

    // --- LOGIQUE DE TRAITEMENT (Handlers) ---

    /**
     * Gère la création et l'affichage visuel de la notification.
     */
    private void handleNotificationDisplay(Message message, Channel targetChannel) {
        Stage popupStage = createPopupStage();
        VBox popupContent = createPopupContent(message);

        // Configuration de l'action au clic
        popupContent.setOnMouseClicked(e -> handleNotificationClick(popupStage, targetChannel));

        Scene scene = new Scene(popupContent);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);

        positionPopup(popupStage);
        animateAndShow(popupStage, popupContent);
    }

    /**
     * Gère l'action utilisateur lors du clic sur la notification.
     */
    private void handleNotificationClick(Stage popupStage, Channel targetChannel) {
        controller.selectChannel(targetChannel);

        bringMainApplicationToFront();

        popupStage.close();
    }

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    /**
     * Remet la fenêtre principale de l'application au premier plan Windows/OS.
     */
    private void bringMainApplicationToFront() {
        // On récupère le Stage principal (la fenêtre de l'appli)
        Stage mainStage = (Stage) Stage.getWindows().filtered(w -> w instanceof Stage).stream().findFirst().orElse(null);

        if (mainStage != null) {
            mainStage.setIconified(false);
            mainStage.show();
            mainStage.toFront();
            mainStage.requestFocus();
        }
    }

    private Stage createPopupStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        return stage;
    }

    private VBox createPopupContent(Message message) {
        VBox content = new VBox(5);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #2f3136; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #202225; -fx-border-width: 2;");
        content.setPrefWidth(320);
        content.setCursor(Cursor.HAND);

        Label titleLabel = new Label("Notification de " + message.getSender().getName());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label bodyLabel = new Label(message.getText());
        bodyLabel.setStyle("-fx-text-fill: #dcddde; -fx-font-size: 12px;");
        bodyLabel.setWrapText(true);

        content.getChildren().addAll(titleLabel, bodyLabel);
        return content;
    }

    private void positionPopup(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMaxX() - 340);
        stage.setY(screenBounds.getMaxY() - 100);
    }

    private void animateAndShow(Stage stage, VBox content) {
        content.setTranslateY(50);
        content.setOpacity(0);
        stage.show();

        // Animation d'entrée
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), content);
        slideIn.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setToValue(1.0);
        new ParallelTransition(slideIn, fadeIn).play();

        // Animation de sortie automatique après 5s
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), content);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> stage.close());
            fadeOut.play();
        });
        delay.play();
    }
}