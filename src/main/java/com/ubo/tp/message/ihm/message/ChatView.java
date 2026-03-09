package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.controller.MessageListController;
import main.java.com.ubo.tp.message.controller.observer.IMessageListObserver;

import javax.swing.*;
import java.awt.*;

public class ChatView extends JPanel implements IMessageListObserver {

    private final MessageListController controller;
    private MessageListView messageListView;
    private JTextField inputField;

    public ChatView(MessageListController controller) {
        this.controller = controller;
        this.initComponents();
        this.controller.addObserver(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // La liste des messages (Centre)
        messageListView = new MessageListView();
        add(messageListView, BorderLayout.CENTER);

        //La barre de saisie (Bas)
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        JButton sendButton = new JButton("Envoyer");

        sendButton.addActionListener(e -> send());
        inputField.addActionListener(e -> send());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void send() {
        String text = inputField.getText();
        if (!text.isEmpty()) {
            controller.sendMessage(text);
            inputField.setText("");
        }
    }

    @Override
    public void onMessageListChanged() {
         messageListView.refresh(controller.getMessages());
    }
}