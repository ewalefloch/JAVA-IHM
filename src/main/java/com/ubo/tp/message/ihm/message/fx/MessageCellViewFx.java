package com.ubo.tp.message.ihm.message.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.common.fx.AbstractCellViewFx;

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
                if (onDeleteAction != null) {
                    onDeleteAction.run();
                }
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