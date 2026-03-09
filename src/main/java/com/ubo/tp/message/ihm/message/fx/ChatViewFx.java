package com.ubo.tp.message.ihm.message.fx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.ubo.tp.message.controller.MessageListController;
import com.ubo.tp.message.controller.observer.IMessageListObserver;

public class ChatViewFx extends BorderPane implements IMessageListObserver {

    private final MessageListController controller;
    private MessageListViewFx messageListView;
    private TextField inputField;

    public ChatViewFx(MessageListController controller,MessageListViewFx messageListView) {
        this.controller = controller;
        this.messageListView = messageListView;
        this.initComponents();
        this.controller.addObserver(this);
    }

    private void initComponents() {
        // liste des messages (Centre)
        this.setCenter(messageListView);

        // barre de saisie (Bas)
        HBox inputPanel = new HBox(10); // Espacement de 10px entre les éléments
        inputPanel.setPadding(new Insets(10));
        inputPanel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");

        inputField = new TextField();
        inputField.setPromptText("Écrivez votre message...");

        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendButton = new Button("Envoyer");
        sendButton.setPrefWidth(100);
        sendButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-weight: bold;");

        // Actions
        sendButton.setOnAction(e -> send());
        inputField.setOnAction(e -> send());

        inputPanel.getChildren().addAll(inputField, sendButton);

        this.setBottom(inputPanel);
    }

    private void send() {
        String text = inputField.getText();
        if (text != null && !text.trim().isEmpty()) {
            controller.sendMessage(text.trim());
            inputField.setText("");
        }
    }

    @Override
    public void onMessageListChanged() {
        Platform.runLater(() -> messageListView.refresh(controller.getMessages()));
    }
}