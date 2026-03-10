package com.ubo.tp.message.ihm.user.fx;

import com.ubo.tp.message.controller.observer.IEasterEggObserver;
import com.ubo.tp.message.ihm.message.fx.EasterEggAnimationFx;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.common.fx.AbstractCellViewFx;

public class UserCellViewFx extends AbstractCellViewFx<User> {

    private Label nameLabel;
    private Label tagLabel;
    private Circle statusIndicator;

    public UserCellViewFx(User user, boolean isMyself, Runnable onMessageAction) {
        super(user);
        if (!isMyself && onMessageAction != null) {
            javafx.scene.control.Button messageBtn = new javafx.scene.control.Button("💬");
            messageBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");

            messageBtn.setOnAction(e -> {
                e.consume();
                onMessageAction.run();
            });

            this.setRight(messageBtn);
            BorderPane.setAlignment(messageBtn, Pos.CENTER);
        }
    }

    @Override
    protected void buildContent() {
        // Indicateur de statut
        statusIndicator = new Circle(6); // Rayon de 6px
        updateStatusIndicator();

        // Panel
        VBox infoPanel = new VBox(3); // 3px d'espacement vertical
        infoPanel.setAlignment(Pos.CENTER_LEFT);

        nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        tagLabel = new Label("@" + item.getUserTag());
        tagLabel.setFont(Font.font("Arial", 12));
        tagLabel.setTextFill(Color.GRAY);

        infoPanel.getChildren().addAll(nameLabel, tagLabel);

        this.setLeft(statusIndicator);
        this.setCenter(infoPanel);

        // Centrer verticalement
        BorderPane.setAlignment(statusIndicator, Pos.CENTER);
        BorderPane.setMargin(statusIndicator, new javafx.geometry.Insets(0, 10, 0, 0));
    }

    private void updateStatusIndicator() {
        if (item.isOnline()) {
            statusIndicator.setFill(Color.web("#43B581")); // Vert
            Tooltip.install(statusIndicator, new Tooltip("En ligne"));
        } else {
            statusIndicator.setFill(Color.LIGHTGRAY);
            Tooltip.install(statusIndicator, new Tooltip("Hors ligne"));
        }
    }

    @Override
    public void refresh() {
        nameLabel.setText(item.getName());
        tagLabel.setText("@" + item.getUserTag());
        updateStatusIndicator();
    }
}