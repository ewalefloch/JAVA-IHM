package com.ubo.tp.message.ihm.channel.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.ubo.tp.message.controller.ChannelListController;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.common.fx.AbstractCellViewFx;

public class ChannelCellViewFx extends AbstractCellViewFx<Channel> {

    private Label nameLabel;
    private Label infoLabel;
    private final ChannelListController controller;

    public ChannelCellViewFx(Channel channel, ChannelListController controller) {
        super(channel);
        this.controller = controller;
        refresh();
        checkDeletable();
    }

    @Override
    protected void buildContent() {
        Label iconLabel = new Label("#");
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        iconLabel.setTextFill(Color.web("#5865F2"));

        VBox infoPanel = new VBox(3);
        infoPanel.setAlignment(Pos.CENTER_LEFT);

        nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        infoLabel = new Label();
        infoLabel.setFont(Font.font("Arial", 11));
        infoLabel.setTextFill(Color.GRAY);

        infoPanel.getChildren().addAll(nameLabel, infoLabel);

        this.setLeft(iconLabel);
        this.setCenter(infoPanel);

        BorderPane.setAlignment(iconLabel, Pos.CENTER);
        BorderPane.setMargin(iconLabel, new javafx.geometry.Insets(0, 10, 0, 0));
    }

    private void checkDeletable() {
        if (controller.isMyChannel(item)) {
            Button deleteBtn = new Button("🗑");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");

            // Éviter que le clic sur le bouton ne sélectionne le canal
            deleteBtn.setOnAction(e -> {
                e.consume();
                controller.deleteChannel(item);
            });

            this.setRight(deleteBtn);
            BorderPane.setAlignment(deleteBtn, Pos.CENTER);
        }
    }

    @Override
    public void refresh() {
        int userCount = item.getUsers().size();
        String creatorTag = item.getCreator().getUserTag();
        String type = controller.isChannelPrivate(item) ? "Privé" : "Public";
        infoLabel.setText(type + " • " + userCount + " membre(s) • Créé par @" + creatorTag);
    }
}