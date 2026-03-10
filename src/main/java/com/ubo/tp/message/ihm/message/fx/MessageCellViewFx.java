package com.ubo.tp.message.ihm.message.fx;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.common.fx.AbstractCellViewFx;
import javafx.util.Duration;

public class MessageCellViewFx extends AbstractCellViewFx<Message> {

    private final boolean isMy;
    private final Runnable onDeleteAction;

    public MessageCellViewFx(Message item, boolean isMy, Runnable onDeleteAction) {
        super(item);
        this.isMy = isMy;
        this.onDeleteAction = onDeleteAction;
        checkDeletable();
    }

    @Override
    protected void buildContent() {
        VBox content = new VBox(5);
        content.setAlignment(Pos.TOP_LEFT);

        Label authorLabel = new Label(item.getSender().getName() + " :");
        authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label messageText = new Label(item.getText());
        messageText.setWrapText(true);
        messageText.setFont(Font.font("Arial", 13));

        messageText.setMaxWidth(1000);

        content.getChildren().addAll(authorLabel, messageText);

        this.setCenter(content);
    }

    private void checkDeletable() {
        if (isMy) {
            Button deleteBtn = new Button("supprimer");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff0000; -fx-cursor: hand;");

            deleteBtn.setOnAction(e -> {
                e.consume();
                deleteBtn.setDisable(true);

                // Initialisation de l'état
                this.setRotate(0);
                this.setTranslateY(0);
                this.setOpacity(1);

                //Balancement initial : 0 à 30 degrés
                RotateTransition swing1 = new RotateTransition(Duration.millis(300), this);
                swing1.setFromAngle(0);
                swing1.setToAngle(15);
                swing1.setInterpolator(Interpolator.EASE_BOTH);

                // Grand balancement : 30 à -30 degrés
                RotateTransition swing2 = new RotateTransition(Duration.millis(500), this);
                swing2.setFromAngle(15);
                swing2.setToAngle(-15);
                swing2.setInterpolator(Interpolator.EASE_BOTH);

                //Retour au centre : -30 à 0 degrés
                RotateTransition swing3 = new RotateTransition(Duration.millis(300), this);
                swing3.setFromAngle(-15);
                swing3.setToAngle(0);
                swing3.setInterpolator(Interpolator.EASE_BOTH);

                // Groupement du balancement complet
                SequentialTransition fullSwing = new SequentialTransition(swing1, swing2, swing3);
                fullSwing.setCycleCount(1);

                // Chute et disparition
                TranslateTransition fall = new TranslateTransition(Duration.millis(600), this);
                fall.setToY(1000);
                fall.setInterpolator(Interpolator.EASE_IN);

                FadeTransition fade = new FadeTransition(Duration.millis(600), this);
                fade.setToValue(0);

                // Enchaînement final
                SequentialTransition totalSequence = new SequentialTransition(
                        fullSwing,
                        new ParallelTransition(fall, fade)
                );

                totalSequence.setOnFinished(ev -> {
                    if (onDeleteAction != null) {
                        onDeleteAction.run();
                    }
                });

                totalSequence.play();
            });

            this.setRight(deleteBtn);
            BorderPane.setAlignment(deleteBtn, Pos.CENTER);
        }
    }

    @Override
    public void refresh() {
        // IGNORE
    }
}