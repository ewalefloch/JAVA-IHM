package com.ubo.tp.message.ihm.message.fx;

import com.ubo.tp.message.controller.ChannelListController;
import com.ubo.tp.message.controller.observer.IChannelSelectionObserver;
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

import com.ubo.tp.message.controller.observer.IChannelListObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;

public class NotificationPopupFx implements IChannelListObserver {

    private final ChannelListController controller;
    public NotificationPopupFx(ChannelListController controller) {
        this.controller= controller;
    }

    @Override
    public void onChannelListChanged() {
        // ignore
    }

    @Override
    public void onNotificationTriggered(Message message, Channel targetChannel, boolean isMention) {
        Platform.runLater(() -> {
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.setAlwaysOnTop(true);

            VBox popupContent = new VBox(5);
            popupContent.setPadding(new Insets(15));
            popupContent.setStyle("-fx-background-color: #2f3136; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #202225; -fx-border-width: 2;");
            popupContent.setPrefWidth(320);

            Label titleLabel = new Label("Notification de " + message.getSender().getName());
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label bodyLabel = new Label(message.getText());
            bodyLabel.setStyle("-fx-text-fill: #dcddde; -fx-font-size: 12px;");
            bodyLabel.setWrapText(true);

            popupContent.getChildren().addAll(titleLabel, bodyLabel);

            Scene scene = new Scene(popupContent);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            popupStage.setX(screenBounds.getMaxX() - 340);
            popupStage.setY(screenBounds.getMaxY() - 100);

            popupContent.setCursor(Cursor.HAND);
            popupContent.setOnMouseClicked(e -> {
                controller.selectChannel(targetChannel);
                popupStage.close();
            });

            popupContent.setTranslateY(50);
            popupContent.setOpacity(0);
            popupStage.show();

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), popupContent);
            slideIn.setToY(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), popupContent);
            fadeIn.setToValue(1.0);
            new ParallelTransition(slideIn, fadeIn).play();

            // Disparition automatique après 5 secondes
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), popupContent);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(ev -> popupStage.close());
                fadeOut.play();
            });
            delay.play();
        });
    }
}