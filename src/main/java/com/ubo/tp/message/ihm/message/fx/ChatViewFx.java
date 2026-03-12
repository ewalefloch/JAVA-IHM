package com.ubo.tp.message.ihm.message.fx;

import com.ubo.tp.message.controller.observer.IEasterEggObserver;
import com.ubo.tp.message.datamodel.User;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.ubo.tp.message.controller.MessageListController;
import com.ubo.tp.message.controller.observer.IMessageListObserver;

import java.util.List;

public class ChatViewFx extends BorderPane implements IMessageListObserver {

    private final MessageListController controller;
    private final MessageListViewFx messageListView;
    private TextField inputField;

    private ContextMenu mentionMenu;

    public ChatViewFx(MessageListController controller, MessageListViewFx messageListView) {
        this.controller = controller;
        this.messageListView = messageListView;
        this.initComponents();
    }

    private void initComponents() {
        this.setCenter(messageListView);

        HBox inputPanel = new HBox(10);
        inputPanel.setPadding(new Insets(10));
        inputPanel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");
        inputPanel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        inputField = new TextField();
        inputField.setPromptText("Écrivez votre message...");
        HBox.setHgrow(inputField, Priority.ALWAYS);


        Label charCountLabel = new Label("0/200");
        charCountLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

        javafx.scene.control.TextFormatter<String> formatter = new javafx.scene.control.TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 200) {
                return null;
            }
            return change;
        });
        inputField.setTextFormatter(formatter);

        inputField.textProperty().addListener((obs, oldText, newText) -> {
            int length = newText.length();
            charCountLabel.setText(length + "/200");

            if (length >= 190) {
                charCountLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 11px;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
            }
        });

        setupMentionAutoComplete();

        Button sendButton = new Button("Envoyer");
        sendButton.setPrefWidth(100);
        sendButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-weight: bold;");

        sendButton.setOnAction(e -> send());
        inputField.setOnAction(e -> send());

        inputPanel.getChildren().addAll(inputField, charCountLabel, sendButton);
        this.setBottom(inputPanel);
    }

    private void setupMentionAutoComplete() {
        mentionMenu = new ContextMenu();

        inputField.textProperty().addListener((obs, oldText, newText) -> {
            String[] words = newText.split(" ");

            if (words.length == 0) {
                mentionMenu.hide();
                return;
            }

            String lastWord = words[words.length - 1];

            if (!lastWord.startsWith("@")) {
                mentionMenu.hide();
                return;
            }

            String query = lastWord.substring(1);
            mentionMenu.getItems().clear();

            List<User> matchingUsers = controller.getCurrentChannelUsers(query);

            for (User u : matchingUsers) {
                MenuItem item = new MenuItem(u.getName() + " (@" + u.getUserTag() + ")");

                item.setOnAction(e -> {
                    String replaced = newText.substring(0, newText.lastIndexOf(lastWord)) + "@" + u.getUserTag() + " ";
                    inputField.setText(replaced);
                    inputField.positionCaret(replaced.length());
                });
                mentionMenu.getItems().add(item);
            }

            if (mentionMenu.getItems().isEmpty()) {
                mentionMenu.hide();
                return;
            }

            Bounds bounds = inputField.localToScreen(inputField.getBoundsInLocal());
            if (bounds != null) {
                mentionMenu.show(inputField, bounds.getMinX(), bounds.getMinY() - (mentionMenu.getItems().size() * 30));
            }
        });
    }

    private void send() {
        String text = inputField.getText();
        if (text != null && !text.trim().isEmpty()) {
            controller.sendMessage(text.trim());
            inputField.setText("");
        }
        if (mentionMenu != null) {
            mentionMenu.hide();
        }
    }

    public MessageListViewFx getMessageListView() {
        return this.messageListView;
    }

    @Override
    public void onMessageListChanged() {
        Platform.runLater(() -> messageListView.refresh(controller.getMessages()));
    }
}