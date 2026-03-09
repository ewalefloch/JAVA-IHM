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

    public ChatViewFx(MessageListController controller) {
        this.controller = controller;
        this.initComponents();
        this.controller.addObserver(this);
    }

    private void initComponents() {
        // La liste des messages (Centre)
        messageListView = new MessageListViewFx();
        this.setCenter(messageListView);

        // La barre de saisie (Bas)
        HBox inputPanel = new HBox(10); // Espacement de 10px entre les éléments
        inputPanel.setPadding(new Insets(10));
        inputPanel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");

        inputField = new TextField();
        inputField.setPromptText("Écrivez votre message...");
        // On fait en sorte que le champ de texte prenne tout l'espace horizontal
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendButton = new Button("Envoyer");
        sendButton.setPrefWidth(100);
        sendButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-weight: bold;");

        // Actions
        sendButton.setOnAction(e -> send());
        inputField.setOnAction(e -> send()); // Touche Entrée

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
        // Toujours Platform.runLater pour les notifications venant du controller
        Platform.runLater(() -> messageListView.refresh(controller.getMessages()));
    }
}